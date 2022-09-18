package com.spring.boot.upload.example.concurrent.advice;

import com.spring.boot.upload.example.concurrent.exception.FileStorageException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(FileStorageException.class)
    public ModelAndView handleException(FileStorageException exception, RedirectAttributes redirectAttributes)
    {
        ModelAndView mv = new ModelAndView();
        mv.addObject("message",exception.getMsg());
        mv.setViewName("error");
        return mv;
    }
}
