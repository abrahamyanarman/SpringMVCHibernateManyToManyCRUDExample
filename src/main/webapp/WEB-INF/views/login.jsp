<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Login page</title>
		<link href="<c:url value='/static/css/bootstrap.css' />"  rel="stylesheet"></link>
		<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
		<link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
	</head>

	<body>
		<div id="mainWrapper">
			<div class="login-container">
				<div class="login-card">
					<div class="login-form">
						<c:url var="loginUrl" value="/login" />
						<form action="${loginUrl}" method="post" class="form-horizontal">
							<c:if test="${param.error != null}">
								<div class="alert alert-danger">
									<p>Invalid username and password.</p>
								</div>
							</c:if>
							<c:if test="${param.logout != null}">
								<div class="alert alert-success">
									<p>You have been logged out successfully.</p>
								</div>
							</c:if>
							<div class="input-group input-sm">
								<label class="input-group-addon" for="username"><i class="fa fa-user"></i></label>
								<input type="text" class="form-control" id="username" name="ssoId" placeholder="Enter Username" required>
							</div>
							<div class="input-group input-sm">
								<label class="input-group-addon" for="password"><i class="fa fa-lock"></i></label> 
								<input type="password" class="form-control" id="password" name="password" placeholder="Enter Password" required>
							</div>
							<div class="input-group input-sm">
                              <div class="checkbox">
                                <label><input type="checkbox" id="rememberme" name="remember-me"> Remember Me</label>  
                              </div>
                            </div>
							<input type="hidden" name="${_csrf.parameterName}"  value="${_csrf.token}" />
								
							<div class="form-actions">
								<input type="submit"
									class="btn btn-block btn-primary btn-default" value="Log in">
							</div>
							<a href="/newuser"
									   class="btn btn-block btn-primary btn-default">Sing in
							</a>

								<%--TODO button for signIn/signUp with facebook--%>
							<a class="btn btn-block btn-social btn-facebook customBtn" href="/loginWithFacebook">
								<span class="fa fa-facebook width42px"></span> Continue With Facebook
							</a>
							<%--TODO button for signIn/signUp with google--%>
							<a class="btn btn-block btn-social btn-google customBtn" href="/loginWithGoogle">
								<span class="fa fa-google width42px"></span> Continue With Google
							</a>
							<%--TODO button for signIn/signUp with linkedIn--%>
							<a class="btn btn-block btn-social btn-linkedin customBtn" href="/loginWithLinkedin">
								<span class="fa fa-linkedin width42px"></span> Continue With LinkedIn
							</a>
							<%--TODO button for signIn/signUp with twitter--%>
                            <a class="btn btn-block btn-social btn-twitter customBtn" href="/loginWithTwitter">
                                <span class="fa fa-twitter width42px"></span> Continue With Twitter
                            </a>
						</form>
					</div>
				</div>
			</div>
		</div>

	</body>
</html>