//存放主要交互逻辑的js代码
// javascript 模块化(package.类.方法)
var seckill = {
    //统一的封装秒杀相关的Ajax的url
    URL: {
        now: function () {                                    //获取系统的当前时间
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {                      //判断是否可以执行秒杀
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {               //执行秒杀(MD5是为了防止使用浏览器插件进行恶意秒杀)
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {      //直接判断对象会看对象是否为空,空就是undefine就是false; isNaN 非数字返回true
            return true;
        } else {
            return false;
        }
    },
    //详情页秒杀逻辑(填写秒杀手机号-->手机号登录)
    detail: {
        init: function (params) {                                //详情页初始化
            //手机验证和登录,计时交互 -->规划我们的交互流程
            var userPhone = $.cookie('killPhone');              //在cookie中查找手机号
            if (!seckill.validatePhone(userPhone)) {            //调用验证手机号的方法
                //绑定手机 控制输出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({                           //弹出输入手机号的BootStrap的模态框
                    show: true,
                    backdrop: 'static',
                    keyboard: false
                });
                $('#killPhoneBtn').click(function () {              //为击点击按钮添加点击事件
                    var inputPhone = $('#killPhoneKey').val();      //获取填写的手机号
                    console.log("inputPhone: " + inputPhone);
                    if (seckill.validatePhone(inputPhone)) {        //如果手机号验证通过了(符合规则)
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});      //就将手机号写入cookie(7天过期)
                        window.location.reload();                   // 同时刷新页面
                    } else {
                        // 手机号验证失败了,错误信息抽取到前端字典里
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }
            //已经登录,计时交互
            var startTime = params['startTime'];        //获取秒杀商品的起止时间以及商品的id
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {            //调用Controller层获取当前系统时间的方法
                if (result && result['success']) {
                    var nowTime = result['data'];                     //得到当前系统的时间
                    //时间判断 计时交互
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result: ' + result);
                    alert('result: ' + result);
                }
            });
        }
    },
    handlerSeckill: function (seckillId, node) {                  //具体秒杀商品的方法,node就是展示倒计时的节点
        //获取秒杀地址,控制显示器,执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');  //将倒计时节点中的内容显示一个按钮,内容是"开始秒杀"
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {      //发送判断是否可以执行秒杀的 POST请求
            if (result && result['success']) {                              //得到并处理是否已可以执行秒杀的结果
                var exposer = result['data'];
                if (exposer['exposed']) {                                   //如果可以执行秒杀
                    var md5 = exposer['md5'];                               //得到秒杀所需要的Md5(防止恶意秒杀)
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl: " + killUrl);
                    $('#killBtn').one('click', function () {                //获取新建的开始秒杀的按钮节点,添加点击时间
                        $(this).addClass('disabled');                       //先禁用按钮,防止用户可以重复点击按钮
                        $.post(killUrl, {}, function (result) {             //防止执行秒杀的POST请求
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                node.html('<span class="label label-success">' + stateInfo + '</span>');        //显示秒杀的结果
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀(浏览器计时偏差)
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.log('result: ' + result);
            }
        });
    },
    countDown: function (seckillId, nowTime, startTime, endTime) {      //计时的方法(需要显示商品信息,剩余时间)
        console.log(seckillId + '_' + nowTime + '_' + startTime + '_' + endTime);
        var seckillBox = $('#seckill-box');                             //获取展示倒计时的节点
        if (nowTime > endTime) {                                        //如果当前的时间大于结束时间,说明秒杀结束
            seckillBox.html('秒杀结束!');
        } else if (nowTime < startTime) {                               //如果当前的时间小于开始时间,说明秒杀还未开始,开始计时
            var killTime = new Date(startTime + 1000);                  //防止时间偏移(延迟)
            seckillBox.countdown(killTime, function (event) {           //调用Jquery countdown插件的countdown方法
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒 ');      //控制时间格式
                seckillBox.html(format);
            }).on('finish.countdown', function () {                   //时间倒计时结束后的回调函数
                console.log('______fininsh.countdown');
                seckill.handlerSeckill(seckillId, seckillBox);        //传入秒杀商品的id,执行秒杀
            });
        } else {                                                    //此时说明秒杀商品的活动已经开始,可以进行秒杀
            seckill.handlerSeckill(seckillId, seckillBox);          //调用秒杀商品的方法
        }
    }
}
