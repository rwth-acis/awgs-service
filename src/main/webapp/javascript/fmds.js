/**
 * @class
 * 
 * FmdClient is a JavaScript client library wrapping the access to the RESTful Web Services for Fake Media Detection created within the lab course
 * Unternehmensgruendung und Neue Medien" in WS 2011/12 at Chair for Computer Science 5 (Databases & Information Systems) at RWTH Aachen University, Germany.
 * 
 * @author <a href="mailto:renzel@dbis.rwth-aachen.de">Dominik Renzel</a>
 * 
 * @returns {FmdClient}
 */
function FmdClient(endpointUrl){
	
	// store a couple of central resource URIs for later usage
	this._serviceEndpoint = endpointUrl;
	this._usersResource = this._serviceEndpoint + "users";
	this._mediaResource = this._serviceEndpoint + "media";
	this._achievementsResource = this._serviceEndpoint + "achievements";
	
	// since RESTful Web Services are stateless by design, credentials will have to be sent
	// with every HTTP request requiring authentication. However, users should only provide
	// credentials once and then play the game. For that purpose we use a simple mechanism
	// that stores credentials in the browser's local storage using a new HTML5 feature. 
	// Works with latest versions of Chrome and Firefox. On initialization of an FmdClient
	// instance, we check, if credentials are stored in local storage and - if so - set two 
	// client fields _uid and _cred, which can be used for authentication in subsequent requests.
	
	if(localStorage.getItem("fmdsuid") !== null && localStorage.getItem("fmdscred") !== null){
		this._uid = localStorage.getItem("fmdsuid");
		this._cred = localStorage.getItem("fmdscred");
	}
	
};

/**
 * Determines asynchronously, if a user with a given login is registered already.
 * The result parameter in the callback function exhibits a value true, if a user 
 * with the given login is already registered, false else.
 * 
 * @param (String) login
 * @param (function(result)) callback
 */
FmdClient.prototype.isRegistered = function(login, callback){
	$.ajax({
		url: this._usersResource + "/" + login,
		type: "GET",
		success: function(da,s){
			callback(true);
		},
		statusCode: {
			404: function(){
				callback(false);
			}
		},
		cache: false
	});
};

/**
 * Determines if a user is currently logged in and returns a boolean value of 
 * true if a user is logged in, false else.
 * 
 * @returns (boolean) 
 */
FmdClient.prototype.isLoggedIn = function(){
	if(typeof this._uid !== 'undefined' && typeof this._cred !== 'undefined'){
		return true;
	} else {
		return false;
	}
};


/**
 * Authenticates a user with the given credentials against the UGNM Web service. 
 * The callback parameter result is a boolean returning true, if authentication succeeded, false else.
 * A side effect of a successful authentication is that credentials are stored in local storage for
 * all later method calls.
 * 
 * @param login (String)
 * @param password (String)
 * @param callback (function(result)) 
 */
FmdClient.prototype.login = function(login, password, callback){
	
	// for this login step we use one HTTP operation on one resource, which is authentication-aware.
	// In this example, we use the GET operation on the resource /users/{login} including credentials
	// as HTTP auth header.
	var resource = this._usersResource + "/" + login;
	var that = this;
	
	// use helper function to create credentials as base64 encoding for HTTP auth header
	var credentials = __make_base_auth(login,password);
	
	// here, you see a first example of an AJAX request done with the help of jQuery.
	$.ajax({
		url: resource, // specify a url to which the HTTP request is sent
		type: "GET", // specify the HTTP operation
		
		// set HTTP auth header before sending request
		beforeSend: function(xhr){
			xhr.setRequestHeader("Authorization", credentials);
		},
		
		// this is one of the callbacks to be triggered on successful processing 
		// of the request by the Web service, in this case a succeeded GET including
		// successful authentication.
		success: function(){
			// set user id and credentials as fields of the client.
			that._uid = login;
			that._cred = credentials;
			
			// store credentials in local storage
			// Local Storage version
			
			localStorage.setItem("fmdsuid",login);
			localStorage.setItem("fmdscred",credentials);
			
			
			// Cookies version
			
			/*
			$.cookie("fmdsuid",login);
			$.cookie("fmdscred",credentials);
			*/
			
			//
			callback(true);
		},
		
		// this is another mechanism of reacting to any answer coming from the Web service.
		statusCode: {
			// if user does not exist, return authentication failed.
			404: function(){
				callback(false);
			},
			// if credentials were not correct, return authentication failed.
			401: function(){
				callback(false);
			} 
		}
	});
};

/**
 * Logs out currently logged in user. Effectively, credentials in local storage and available as fields
 * _uid and _cred will be reset.
 */
FmdClient.prototype.logout = function(){
	
	// remove credentials from local storage
	localStorage.removeItem("fmdsuid");
	localStorage.removeItem("fmdscred");
	
	// reset client fields
	delete this._uid;
	delete this._cred;
};

/**
 * Signs up a new user with given login, name and password against the UGNM Web service asynchronously.  
 * The callback parameter result is a JSON object of the form
 * 
 * 		result = {status: <STATUS>(, uri: <URI>)};
 * 
 * In the case of a successful signup, <STATUS> will contain the value "created" and <URI> the URI to 
 * the newly created resource. In the case of a failed signup, <STATUS> will contain a message about
 * the error occurred, and <URI> remains unset.
 * 
 * @param login (String)
 * @param name (String)
 * @param password (String)
 * @param callback (function(result)) 
 */
FmdClient.prototype.signup = function(login, name, password, callback){
	
	// create JSON representation to be passed to the Web Service
	var d = {};
	d.login = login;
	d.name = name;
	d.pass = password;
	
	var resource = this._usersResource;
	
	// do AJAX call to Web Service using jQuery.ajax
	$.ajax({
		url: resource,
		type: "POST",
		data: JSON.stringify(d), // JSON data must be transformed to a String representation
		
		// process result in case of success and feed result to callback function passed by developer
		success: function(uri){
			var result = {};
			result.status = "created";
			result.uri = uri;
			
			callback(result);
		},
		// process result in case of different HTTP statuses and feed result to callback function passed by developer
		statusCode: {
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

/**
 * Retrieves all users asynchronously. The result parameter of the callback function 
 * contains the list of all retrieved users as an array of JSON objects of the form
 * 
 *  	[<USER1>,...,<USERn>]
 *  
 * where each <USERx> contains the following fields:
 * 
 * <ul>
 * 	<li>login - Login</li>
 * 	<li>name - Full Name</li>
 * 	<li>xp - Experience Points</li>
 *  <li>resource - URL to the User Resource</li>
 * </ul>
 * 
 * @param callback (function(result)) 
 */
FmdClient.prototype.getUsers = function(callback){
	
	var resource = this._usersResource;
	
	$.get(resource, function(data) {
		callback(data.users);		
	});
};

/**
 * Retrieves all media asynchronously. The result parameter of the callback function 
 * contains the list of all retrieved media as an array of JSON objects of the form
 * 
 *  	[<MEDIUM1>,...,<MEDIUMn>]
 *  
 * where each <MEDIUMx> contains the following fields:
 * 
 * <ul>
 * 	<li>url - Media URL</li>
 * 	<li>description - Fulltext Description of the Medium</li>
 *  <li>resource - URL to the Medium Resource</li>
 * </ul>
 * 
 * @param callback (function(result)) 
 */
FmdClient.prototype.getMedia = function(callback){
	var resource = this._mediaResource;
	
	$.get(resource, function(data) {
		callback(data.media);		
	});
};

FmdClient.prototype.getMediaRatings = function(m, callback){
	var resource = this._mediaResource + "/" + m.id + "/ratings";
	
	$.get(resource, function(data) {
		callback(m,data.ratings);		
	});
};

FmdClient.prototype.rateMedium = function(m, r, callback){
	
	if(!this.isLoggedIn){
		alert("Not logged in");
	} 
	var resource = this._mediaResource + "/" + m.id + "/ratings";
	var d = {rating: r};
	
	var that = this;
	
	// do AJAX call to Web Service using jQuery.ajax
	$.ajax({
		url: resource,
		type: "POST",
		data: JSON.stringify(d), // JSON data must be transformed to a String representation
		beforeSend: function(xhr){
			xhr.setRequestHeader("Authorization", that._cred);
		},
		// process result in case of success and feed result to callback function passed by developer
		success: function(uri){
			var result = {};
			result.status = "rated";
			result.uri = uri;
			
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
}

//TODO: add further library functions

// Private helper function to create Base64 encoded credentials as needed for
// HTTP basic authentication
function __make_base_auth(user, password) {
	var tok = user + ':' + password;
	var hash = $.base64.encode(tok);
	var result = "Basic " + hash;
	return result;
}