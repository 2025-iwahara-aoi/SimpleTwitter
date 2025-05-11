<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>${loginUser.account}の設定</title>
		<link href="css/style.css" rel="stylesheet" type="text/css">
		</head>
	<body>
		<div class="main-contents">

		<c:if test="${ not empty errorMessages }">
				<div class="errorMessages">
					<ul>
						<c:forEach items="${errorMessages}" var="errorMessage">
							<li><c:out value="${errorMessage}" />
						</c:forEach>
					</ul>
				</div>
		</c:if>

			<form action="edit" method="post"><br />
				<input name="messageId" value="${message.id}" id="id" type="hidden"/>

				<label for="description">つぶやく</label>
				<pre><textarea name="text" cols="70" rows="10" id="text"><c:out value="${message.text}" /></textarea><br /></pre>
				<input type="submit" value="更新" /> <br />
				<a href="./">戻る</a>
			</form>

			<div class="copyright"> Copyright(c)Your Name</div>
		</div>
	</body>
</html>