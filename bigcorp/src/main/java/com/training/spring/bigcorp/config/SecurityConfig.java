package com.training.spring.bigcorp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    public static final String ADMIN = "ADMIN";
    public static final String ROLE_ADMIN = "ROLE_"+ADMIN;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HandlerInterceptorAdapter securityInfoInterceptor(){
        return new HandlerInterceptorAdapter() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse
                    response, Object handler, ModelAndView modelAndView) {
                if(modelAndView!=null){
                    modelAndView.addObject("logged", request.getUserPrincipal() != null);
                    modelAndView.addObject("_csrf", request.getAttribute("_csrf"));
                    if(request.getRequestURI() != null && request.getQueryString() !=null){
                        modelAndView.addObject("error",
                                request.getRequestURI().equals("/formLogin") &&
                                        request.getQueryString().contains("error"));
                    }
                }
            }
        };
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
        jdbcUserDetailsManager.setDataSource(dataSource);
        jdbcUserDetailsManager.createUser(User.builder().username("user").password(bCryptPasswordEncoder().encode("password")).roles("USER").build());
        jdbcUserDetailsManager.createUser(User.builder().username("userAdmin").password(bCryptPasswordEncoder().encode("passwordAdmin")).roles(ADMIN).build());
        return jdbcUserDetailsManager;
    }

    @Bean
    public WebSecurityConfigurerAdapter webSecurityConfig(DataSource dataSource) {
        return new WebSecurityConfigurerAdapter() {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests()
                        .antMatchers("/css/**", "/img/**").permitAll()
                        .antMatchers("/*/create/**").hasRole(ADMIN)
                        .anyRequest().authenticated()
                        .and().formLogin().loginPage("/formLogin").loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password").permitAll()
                        .and().headers().frameOptions().disable()
                        .and().csrf().ignoringAntMatchers("/console/**");
            }

            @Override
            protected void configure(AuthenticationManagerBuilder builder) throws Exception {
                builder.jdbcAuthentication()
                        .passwordEncoder(new BCryptPasswordEncoder())
                        .dataSource(dataSource);
            }
        };
    }
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("index");
                registry.addViewController("/formLogin").setViewName("login");
            }

            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(securityInfoInterceptor());
            }
        };
    }
}
