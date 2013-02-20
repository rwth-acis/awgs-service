/**
 * @class
 * 
 * awgs.js
 * 
 * A JavaScript client library for the AWGS RESTful API. 
 * 
 * Chair for Computer Science 5 (Databases & Information Systems) at RWTH Aachen University, Germany.
 * 
 * @author <a href="mailto:renzel@dbis.rwth-aachen.de">Dominik Renzel</a>
 * 
 * @returns {awgs}
 */
function AwgsClient(endpointUrl){
	
	// store a couple of central resource URIs for later usage
	this._serviceEndpoint = endpointUrl;
	this._itemsResource = this._serviceEndpoint + "items";
	this._itemTypesResource = this._serviceEndpoint + "items/types";
	this._authResource = this._serviceEndpoint + "auth";
	
	// since RESTful Web Services are stateless by design, credentials will have to be sent
	// with every HTTP request requiring authentication. However, users should only provide
	// credentials once and then use the client. For that purpose we use a simple mechanism
	// that stores credentials in the browser's local HTML5 storage. 
	//
	// Credentials consist of a Jabber ID <jid> and a password <pw> are stored as string 
	//
	//    "Basic " + base64('<jid>:<pw>'  
	
	
	if(localStorage.getItem("jidcred") !== null){
		this._jidcred = localStorage.getItem("jidcred");
	}
	
};

/**
 * Determines if a user is currently logged in and returns a boolean value of 
 * true if a user is logged in, false else.
 * 
 * @returns (boolean) 
 */
AwgsClient.prototype.authenticated = function(){
	if(typeof this._jidcred !== 'undefined'){
		return true;
	} else {
		return false;
	}
};


/**
 * Authenticates a user with the given XMPP credentials against the AWGS Web service. 
 * The callback parameter result is a boolean returning true, if authentication succeeded, false else.
 * A side effect of a successful authentication is that credentials are stored in local storage for
 * all later method calls, such that authenticated() = true from that moment on until logout.
 * 
 * @param jid (String)
 * @param password (String)
 * @param callback (function(result)) 
 */
AwgsClient.prototype.authenticate = function(jid, password, callback){
	
	// for this login step we use one HTTP operation on one resource, which is authentication-aware.
	// In this example, we use the GET operation on the resource /auth including credentials
	// as HTTP auth header.
	var resource = this._authResource;
	var that = this;
	
	// use helper function to create credentials as base64 encoding for HTTP auth header
	var hash = __make_base_auth(jid,password);
	
	// here, you see a first example of an AJAX request done with the help of jQuery.
	$.ajax({
		url: resource, // specify a url to which the HTTP request is sent
		type: "GET", // specify the HTTP operation
		
		// set HTTP auth header before sending request
		beforeSend: function(xhr){
			xhr.setRequestHeader("Authorization", "Basic " + hash);
		},
		
		// this is one of the callbacks to be triggered on successful processing 
		// of the request by the Web service, in this case a succeeded GET including
		// successful authentication.
		success: function(){
		
			that._jidcred = hash;
			
			// store credentials in local storage
			// Local Storage version
			
			localStorage.setItem("jidcred",hash);
			
			// Cookies version
			
			/*
			$.cookie("jidcred",credentials);
			*/
			
			//
			callback(true);
		},
		
		statusCode: {
			// if credentials were not correct, return authentication failed.
			401: function(){
				callback(false);
			} 
		}
	});
};

AwgsClient.prototype.getUser = function(){
	if(typeof this._jidcred !== 'undefined'){
		var dec = $.base64.decode(this._jidcred);
		return dec.split(":")[0];
	}
	return null;
}

/**
 * Logs out currently logged in user. Effectively, credentials in local storage and available as fields
 * _uid and _cred will be reset.
 */
AwgsClient.prototype.logout = function(){
	
	// remove credentials from local storage
	localStorage.removeItem("jidcred");
	
	// reset client fields
	delete this._jidcred;
};

/**
 * Retrieves all items asynchronously. The result parameter of the callback function 
 * contains the list of all retrieved items as an array of JSON objects of the form
 * 
 *  	[<Item1>,...,<Itemn>]
 *  
 * where each <Itemx> contains the following fields:
 * 
 * <ul>
 * 	<li>url - Media URL</li>
 * 	<li>description - Fulltext Description of the Medium</li>
 *  <li>resource - URL to the Medium Resource</li>
 * </ul>
 * 
 * @param callback (function(result)) 
 */
AwgsClient.prototype.getItems = function(callback){
	var resource = this._itemsResource;
	
	$.get(resource, function(data) {
		callback(data.items);		
	});
};

AwgsClient.prototype.searchItems = function(query, callback){
	var resource = this._itemsResource + "?q=" + query;
	
	$.get(resource, function(data) {
		var ar;
		if(typeof data.items == 'undefined'){
			ar = [];
		}
		else if(data.items.length){
			ar = data.items;
		}
		else{
			var ar = [];
			ar.push(data.items);
		}
		callback(ar);		
	});
};

AwgsClient.prototype.searchItemTypes = function(query, callback){
	var resource = this._itemTypesResource + "?q=" + query;
	
	$.get(resource, function(data) {
		var ar;
		if(typeof data.itemtypes == 'undefined'){
			ar = [];
		}
		else if(data.itemtypes.length){
			ar = data.itemtypes;
		}
		else{
			var ar = [];
			ar.push(data.itemtypes);
		}
		callback(ar);		
	});
};

AwgsClient.prototype.getItem = function(id, callback){
	var resource = this._itemsResource + "/" + id;
	
	$.getJSON(resource, function(data) {
		callback(data);		
	});
};

AwgsClient.prototype.deleteItem = function(id, callback){	
	if(!this.authenticated){
		alert("You must be authenticated for this operation! Please log in first.");
	} 
	var resource = this._itemsResource + "/" + id;
	
	var that = this;
	
	// do AJAX call to Web Service using jQuery.ajax
	$.ajax({
		url: resource,
		type: "DELETE",
		beforeSend: function(xhr){
			xhr.setRequestHeader("Authorization", "Basic " + that._jidcred);
		},
		// process result in case of success and feed result to callback function passed by developer
		success: function(uri){
			var result = {};
			result.status = "success";
			result.uri = uri;
			
			callback(result);
		},
		// process result in case of different HTTP statuses and feed result to callback function passed by developer
		statusCode: {
			401: function(){
				callback({status:"unauthorized"});
			},
			404: function(){
				callback({status:"notfound"});
			},
			500: function(){
				callback({status:"servererror"});
			},
			
		},
		contentType: "application/json",
		cache: false
	});
};

AwgsClient.prototype.updateItem = function(id,m,callback){
	
	if(!this.authenticated){
		alert("You must be authenticated for this operation! Please log in first.");
	} 
	var resource = this._itemsResource + "/" + id;
	
	var that = this;
	
	$.ajax({
		url: resource,
		type: "PUT",
		data: JSON.stringify(m),
		beforeSend: function(xhr){
			xhr.setRequestHeader("Authorization", "Basic " + that._jidcred);
		},
		// process result in case of success and feed result to callback function passed by developer
		success: function(data,status,xhr){
			var result = {};
			result.status = status;
			result.data = data;
			callback(result);
		},
		// process result in case of different HTTP statuses and feed result to callback function passed by developer
		statusCode: {
			400: function(){
				callback({status:"badrequest"});
			},
			401: function(){
				callback({status:"unauthorized"});
			},
			404: function(){
				callback({status:"notfound"});
			},
			500: function(){
				callback({status:"servererror"});
			},
			
		},
		contentType: "application/json",
		cache: false
	});
}

AwgsClient.prototype.createItemType = function(m, callback){
	
	if(!this.authenticated){
		alert("You must be authenticated for this operation! Please log in first.");
	} 
	var resource = this._itemTypesResource;
	
	var that = this;
	
	// do AJAX call to Web Service using jQuery.ajax
	$.ajax({
		url: resource,
		type: "POST",
		data: JSON.stringify(m), // JSON data must be transformed to a String representation
		beforeSend: function(xhr){
			xhr.setRequestHeader("Authorization", "Basic " + that._jidcred);
		},
		// process result in case of success and feed result to callback function passed by developer
		success: function(data,status,xhr){
			var result = {};
			result.status = status;
			result.data = data;
			callback(result);
		},
		// process result in case of different HTTP statuses and feed result to callback function passed by developer
		statusCode: {
			400: function(){
				callback({status:"badrequest"});
			},
			401: function(){
				callback({status:"unauthorized"});
			},
			404: function(){
				callback({status:"notfound"});
			},
			409: function(){
				callback({status:"conflict"});
			},
			500: function(){
				callback({status:"servererror"});
			},
			
		},
		contentType: "application/json",
		cache: false
	});
};

AwgsClient.prototype.createItem = function(m, callback){
	
	if(!this.authenticated){
		alert("You must be authenticated for this operation! Please log in first.");
	} 
	var resource = this._itemsResource;
	
	var that = this;
	
	// do AJAX call to Web Service using jQuery.ajax
	$.ajax({
		url: resource,
		type: "POST",
		data: JSON.stringify(m), // JSON data must be transformed to a String representation
		beforeSend: function(xhr){
			xhr.setRequestHeader("Authorization", "Basic " + that._jidcred);
		},
		// process result in case of success and feed result to callback function passed by developer
		success: function(data,status,xhr){
			var result = {};
			result.status = status;
			result.data = data;
			
			callback(result);
		},
		// process result in case of different HTTP statuses and feed result to callback function passed by developer
		statusCode: {
			400: function(){
				callback({status:"badrequest"});
			},
			401: function(){
				callback({status:"unauthorized"});
			},
			404: function(){
				callback({status:"notfound"});
			},
			409: function(){
				callback({status:"conflict"});
			},
			500: function(){
				callback({status:"servererror"});
			},
			
		},
		contentType: "application/json",
		cache: false
	});
};

AwgsClient.prototype.getNextId = function(callback){
	var resource = this._itemsResource + "/next";
	
	$.get(resource, function(data) {
		callback(data.id);		
	});
};



//TODO: add further library functions

// Private helper function to create Base64 encoded credentials as needed for
// HTTP basic authentication
function __make_base_auth(jid, password) {
	var tok = jid + ':' + password;
	return $.base64.encode(tok);
}

//Private helper function to create Base64 encoded credentials as needed for
//HTTP basic authentication
function __get_base_auth(b64) {
	var org = $.base64.decode(tok);
	return org;
}

