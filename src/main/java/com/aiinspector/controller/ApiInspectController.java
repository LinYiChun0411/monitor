package com.aiinspector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.aiinspector.config.YAMLConfig;
import com.aiinspector.service.ApiInspectFailLogService;
import com.aiinspector.service.ApiInspectStatusService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/apiinspect")
@Slf4j
public class ApiInspectController {
	
	@Autowired
	private ApiInspectFailLogService apiInspectFailLogService;
	
	@Autowired
	private ApiInspectStatusService apiInspectStatusService;
	
	@Autowired
	private  YAMLConfig myConfig;

	
	@GetMapping("/today")
    public String today(final Model model) {

        IReactiveDataDriverContextVariable reactiveDataDrivenMode =
                new ReactiveDataDriverContextVariable(apiInspectStatusService.getAllStatusToday());

        model.addAttribute("status", reactiveDataDrivenMode);


        return "index";
    }

}


