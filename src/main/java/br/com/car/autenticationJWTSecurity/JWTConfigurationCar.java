package br.com.car.autenticationJWTSecurity;

import br.com.car.services.UserJWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class JWTConfigurationCar extends WebSecurityConfigurerAdapter {

    @Autowired
    private final UserJWTService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private TokenManager tokenMnager;


    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }


    public JWTConfigurationCar(UserJWTService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    //Usando as classe que conmfiguramos anteriormente como base
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }
    //Como o Spring Secutirty deve entender minha pag


    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable(); Resolve ataques a aplicação, está desativado por ser ambiente dev
//        Autorizações das requisições
//        Permite que metódo post seja feito sem restrições, as demais precisa de autenticação
//        Filtro de autenticação
//        Filtro de Validação
//        Quando usamos Aplicações Rest queremos um ambiente stataless, não grava estado

        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST,"/user").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/h2/**").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable()
                .and()
                .addFilter(new JWTAtutenticationFilter(authenticationManager()))
                .addFilter(new JWTValidationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ;
    }
    //        Configuração do cors
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}

//.antMatchers("/**").permitAll()  permite todas as rotas

