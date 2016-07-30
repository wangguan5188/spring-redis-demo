package com.omnipotent.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.omnipotent.util.RedisUtil;

@Controller
public class MainController {
	
	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request, ModelMap modelMap) {
		String thelo = "thelothelothelothelothelo";
		RedisUtil.setData("thelo", thelo);
		
		String str = RedisUtil.getData("thelo");
		System.out.println(str);
		RedisUtil.deleteData("thelo");
		System.out.println(RedisUtil.getData("thelo"));
		
		return new ModelAndView("index", modelMap);
	}
	
}
