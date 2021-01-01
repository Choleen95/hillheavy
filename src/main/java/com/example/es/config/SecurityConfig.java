package com.example.es.config;

import com.example.es.pojo.MyUserDetails;
import com.example.es.service.UserService;
import com.example.es.service.impl.UserServiceImpl;
import com.example.es.vo.ResponseBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Choleen
 * @since 2020/12/25 23:45
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserServiceImpl userServiceImpl;

    @Resource
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceImpl).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略拦截
        web.ignoring().antMatchers("/register","/js/**","/images/**","/css/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()//开启登录
               //表示访问，ex/index 这个接口，需要具备admin角色
                .antMatchers("/es/**").hasRole("dba")
               .antMatchers("/admin/**").hasRole("admin")
               .antMatchers("/welcome/**").hasRole("user")
               //表示剩余的其他接口，登录之后能访问
                .anyRequest().authenticated()
                .and()
                .formLogin()
               //登录处理接口
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
               //定义登录成功的处理器
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        response.sendRedirect("/success.html");
                        MyUserDetails userName = (MyUserDetails)authentication.getPrincipal();
                        Object credentials = authentication.getCredentials();
                        System.out.println(userName+" "+credentials);
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException ex) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        ResponseBean responseBean = new ResponseBean();
                        if(ex instanceof LockedException){
                            responseBean.setMessage("账户被锁定，请联系管理员!");
                        }else if(ex instanceof CredentialsExpiredException){
                            responseBean.setMessage("密码过期，请联系管理员!");
                        }else if(ex instanceof AccountExpiredException){
                            responseBean.setMessage("账户过期，请联系管理员!");
                        }else if(ex instanceof DisabledException){
                            responseBean.setMessage("账户被禁用，请联系管理员!");
                        }else if(ex instanceof BadCredentialsException){
                            responseBean.setMessage("密码或用户名输入错误，请重新输入!");
                        }
                        responseBean.setCode(400);
                        String result = new ObjectMapper().writeValueAsString(responseBean);
                        out.write(result);
                        out.flush();
                    }
                })
               //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.setContentType("application/json;charset=utf-8");
                        PrintWriter out = response.getWriter();
                        out.write("you have login out success !");
                        out.flush();
                    }
                })
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((req,resp,authException) -> {
                      resp.sendRedirect("/login.html");
                });

    }

    /**
     * 生成密码
     * @param
     * @author Choleen
     * @date 2020/12/28 21:35
     * @return {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_dba > ROLE_admin > ROLE_user");
        return hierarchy;
    }


    @Override
    @Bean
    protected UserDetailsService userDetailsService(){
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        if(!manager.userExists("九月的山沉")){
            manager.createUser(org.springframework.security.core.userdetails.User.withUsername("九月的山沉")
                    .password("123").roles("user").build());
        }
        if(!manager.userExists("admin")){
            manager.createUser(User.withUsername("admin").password("123").roles("admin").build());
        }
        return manager;
    }
}
