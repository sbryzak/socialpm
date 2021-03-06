package com.ocpsoft.socialpm.web.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.socialpm.util.Timer;

@WebFilter(urlPatterns = { "/*" })
public class ResponseTimeLoggingFilter implements Filter
{
   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
   {
      if (DispatcherType.REQUEST.equals(request.getDispatcherType())
               && !((HttpServletRequest) request).getRequestURI().contains(".xhtml"))
      {
         Timer timer = Timer.getTimer().start();
         chain.doFilter(request, response);
         timer.stop();
         double time = timer.getElapsedMilliseconds();
         System.out.println("Request completed in [" + (time / 1000.0) + "] seconds: ["
                  + ((HttpServletRequest) request).getRequestURI() + "]");
      }
      else
         chain.doFilter(request, response);
   }

   @Override
   public void init(final FilterConfig filterConfig)
   {}

   @Override
   public void destroy()
   {}
}