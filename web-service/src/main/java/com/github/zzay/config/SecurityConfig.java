package com.github.zzay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author zzay
 * @className SecurityConfig
 * @description Spring Security Configuration
 * @create 2022/04/30 18:56
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * White list of authentications
     */
    public static final String[] AUTH_WHITELIST = {
            "/",
            "/test/hello",
            "/user/login"
    };

    /**
     * Set customized {@link UserDetailsService}
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configurations on {@link AuthenticationManagerBuilder}
     *
     * @param auth {@link AuthenticationManagerBuilder}
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * Configurations on {@link HttpSecurity}
     *
     * @param http {@link HttpSecurity}
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
//                .loginPage("login.html")
                .loginProcessingUrl("/user/login")
                .defaultSuccessUrl("/test/index").permitAll()
            .and().logout()
                .logoutUrl("/logout").logoutSuccessUrl("/login").permitAll()
            .and().authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/findAll").hasRole("管理员")
                .antMatchers("/find").hasAnyAuthority("menu:system")
                .anyRequest().authenticated()
            .and().exceptionHandling()
                .accessDeniedPage("/security/unauth")
                .and().csrf().disable();
    }

    /**
     * Set {@link BCryptPasswordEncoder} as Password encoder
     *
     * @return Password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
