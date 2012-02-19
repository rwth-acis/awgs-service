package de.rwth.dbis.ugnm.util;

import com.sun.jersey.core.util.Base64;

import de.rwth.dbis.ugnm.entity.User;

public class Authentication {
	// Little gift from your tutors...
	// A simple authentication mechanism;
	// For use in one of the defined operations by referring 
	// to @HeaderParam("authorization") for authHeader.
	public static boolean authenticated(String authHeader,User u){
		if(authHeader != null){
			String[] dauth = null;
			String authkey = authHeader.split(" ")[1];
			if(Base64.isBase64(authkey)){
				dauth = (new String(Base64.decode(authkey))).split(":");
				System.out.println("Login - Should: " + u.getLogin() + " Is: " + dauth[0]);
				System.out.println("Pass - Should: " + u.getPass() + " Is: " + dauth[1]);
				if(dauth[0].equals(u.getLogin()) && dauth[1].equals(u.getPass())){
					return true;
				}
			}
		}
		return false;
	}

	public static String[] parseAuthHeader(String authHeader){
		if(authHeader != null){
			String authkey = authHeader.split(" ")[1];
			if(Base64.isBase64(authkey)){
				String[] dauth = (new String(Base64.decode(authkey))).split(":");
				if(dauth.length == 2){
					return dauth;
				}
				else {
					throw new IllegalArgumentException("Auth header must contain login and password separated by a colon!");
				}
			}else{
				throw new IllegalArgumentException("Auth header must be valid Base64!");
			}
		}
		else throw new IllegalArgumentException("Auth header must be specified!");
	}

}
