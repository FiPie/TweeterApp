package lu.lllc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
public class Security extends WebSecurityConfigurerAdapter {

	private DataSource dataSource;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder())
				.usersByUsernameQuery("SELECT email, password, true FROM users WHERE email=?")
				.authoritiesByUsernameQuery(
						"SELECT u.email, ur.roles FROM user_roles ur JOIN users u ON u.id = ur.user_id WHERE u.email = ?");
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage( "/login" )
                .usernameParameter( "email" )
                .passwordParameter( "password" )
                .successHandler( myAuthenticationSuccessHandler() )               //new one
                //.defaultSuccessUrl( "/", true )                               //old one
                .and()
                .authorizeRequests()

                .antMatchers( "/login" ).anonymous()
                .antMatchers( "/" ).permitAll()
                .antMatchers( "/js/**" ).permitAll()
                .antMatchers( "/css/**" ).permitAll()
                .antMatchers( "/api/**" ).permitAll()							//api usage
				/*
				 * .antMatchers( "/api/likers" ).permitAll() .antMatchers( "/api/likers/**"
				 * ).permitAll() .antMatchers( "/api/ourJSON" ).permitAll() .antMatchers(
				 * "/api/ourJSON/**" ).permitAll()
				 */
                .antMatchers( "/user/*/tweets" ).permitAll()
                .antMatchers( "/user/search-tweets" ).permitAll()
                .antMatchers( "/user/all" ).permitAll()
                .antMatchers( "/user/add" ).anonymous()
                .antMatchers( "/user/delete/**" ).hasAnyRole("ADMIN","USER")
                .antMatchers( "/user/edit/**" ).hasAnyRole("ADMIN","USER")
                .antMatchers( "/tweet/list" ).permitAll()
                .antMatchers( "/tweet/add" ).authenticated()
                .antMatchers( "/tweet/*/like" ).authenticated()
                .antMatchers( "/tweet/*/dislike" ).authenticated()
                .antMatchers( "/admin/**" ).hasRole( "ADMIN" )
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .logout().logoutUrl( "/logout" ).invalidateHttpSession( true ).clearAuthentication( true )
                .logoutSuccessUrl( "/" );

    }
	
    @Bean           //new one
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new MySimpleUrlAuthenticationSuccessHandler();
    }

}
