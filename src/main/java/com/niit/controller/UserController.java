package com.niit.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.dao.UsersDao;
import com.niit.model.Users;
import com.niit.model.Error;

@RestController
public class UserController 
{
    @Autowired
    
    private UsersDao usersDao;
    @RequestMapping(value="/registration",method=RequestMethod.PUT)
    public ResponseEntity<Void> createUser(@RequestBody Users user) {
        System.out.println("Creating User " + user.getFirstname());
 
 
        usersDao.registration(user);
 
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
    
    
  
    
   /* 
    public ResponseEntity<Void> registration(@RequestBody Users user)
    {
        try
        {
            user.setEnabled(true);
            List<Users> users=usersDao.getAllUsers();
            //for(T var:collection)
            for(Users u:users)
            {
                if(u.getUsername().equals(user.getUsername()))
                {
                    Error error=new Error(2,"Username already exists");
                    return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);    
                }
            }
            System.out.println(user.getUsername() + "" + user.getFirstname() + "" + user.getEmail());
            usersDao.registration(user);
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }
        catch(Exception e)
        {
            Error error=new Error(1,"cannot register user details..");
            return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }   */ 

	@RequestMapping(value="/login",method=RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody Users users,HttpSession session)
	{ 
	    System.out.println("Is Session New For" + users.getUsername() + session.isNew());
	    Users validUser=usersDao.login(users);
	    if(validUser==null)
	    {
		    Error error=new Error(3,"Invalid username and password.. please enter valid credentials");
		    return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		else
		{
		    validUser.setOnline(true);
		    validUser=usersDao.updateUser(validUser);
		    session.setAttribute("user", validUser);
		    return new ResponseEntity<Users>(validUser,HttpStatus.OK);    
		}
	}
	
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public ResponseEntity<?>logout(HttpSession session)
	{    
	    Users users=(Users)session.getAttribute("user");
	    if(users==null)
	    {
	        Error error=new Error(3,"Unauthorized user");
	        return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED); 
	    }
	    System.out.println("Is Session New for" + users.getUsername() + session.isNew());
	    users.setOnline(false);
	    usersDao.updateUser(users);
	    session.removeAttribute("user");
	    session.invalidate();
	    return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
