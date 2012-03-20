package de.rwth.dbis.acis.awgs.util;

import com.sun.jersey.core.util.Base64;

public class Authentication {

	public static String[] parseAuthHeader(String authHeader){
		if(authHeader != null){
			String authkey = authHeader.split(" ")[1];
			if(Base64.isBase64(authkey)){
				String[] dauth = (new String(Base64.decode(authkey))).split(":");
				if(dauth.length == 2){
					if(dauth[0].split("@").length == 2){
						return dauth;
					}
					else{
						throw new IllegalArgumentException("Auth header must contain login as Jabber ID!");
					}
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
