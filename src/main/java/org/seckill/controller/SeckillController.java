package org.seckill.controller;

import org.seckill.Enum.SeckillStatEnum;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.impl.SeckillServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * 秒杀API的Controller层
 */
@Controller
@RequestMapping("/seckill")     //表示的是模块 Url// -->/模块/xxx/xxx...
public class SeckillController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());      //打印日志信息
    @Autowired
    private SeckillServiceImpl seckillService;      //注入Service
    @GetMapping ("/list")
    public String list(Map<String,Object> map){                                 //获取所有的商品信息
        List<Seckill> list=seckillService.getSeckillList();
        map.put("list",list);
        return "list";
    }
    @GetMapping("/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Map<String,Object> map){         //显示商品的详情页
        if(seckillId==null){                                                                        //如果商品的id不存在,就返回列表页面
            return "redirect:/seckill/list.jsp";
        }
        Seckill seckill=seckillService.getById(seckillId);
        if(seckill==null){                                                                        //如果根据查询的商品不存在,返回列表页面
            return "forward:/seckill/list.jsp";
        }
        map.put("seckill",seckill);
        return "detail";
    }
    @ResponseBody
    @PostMapping("/{seckillId}/exposer")//返回的Json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){              //判断是不是可以执行秒杀
        SeckillResult<Exposer> result;
      try {
          Exposer exposer=seckillService.exportSeckillUrl(seckillId);
          result=new SeckillResult<Exposer>(true,exposer);
      }catch (Exception e){
          logger.error(e.getMessage(),e);
          result=new SeckillResult<Exposer>(false,e.getMessage());
      }
        return result;
    }
    @ResponseBody
    @PostMapping("/{seckillId}/{md5}/execution")
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone",required = false) Long userPhone){
        if(userPhone ==null){           //绕过前端的验证,没有输入手机号
            return new SeckillResult<SeckillExecution>(false,"未注册手机");
        }
        SeckillResult<SeckillExecution> result;
        try {
            //SeckillExecution execution=seckillService.executeSeckill(seckillId,userPhone,md5);      //执行秒杀
            SeckillExecution execution=seckillService.executeSeckillByProcedure(seckillId,userPhone,md5);     // 通过存储过程执行秒杀
            return new SeckillResult<SeckillExecution>(true,execution);
        }
        catch (RepeatKillException e1)                          //如果重复秒杀
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillCloseException e2)                    //如果秒杀关闭了
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
    }

    @ResponseBody
    @GetMapping("/time/now")
    public SeckillResult<Long> time(){                    //获取系统的时间
        Date now=new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }
}


























