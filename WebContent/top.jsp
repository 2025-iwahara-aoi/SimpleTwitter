<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>簡易Twitter</title>
<link href="./css/style.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div class="main-contents">
		<div class="header">

			<c:if test="${ empty loginUser }">
				<%-- ログインしていない場合 --%>
				<a href="login">ログイン</a>
				<a href="signup">登録する</a>
			</c:if>
			<c:if test="${ not empty loginUser }">
				<%-- ログイン済みの場合 --%>
				<a href="./">ホーム</a>
				<a href="setting">設定</a>
				<a href="logout">ログアウト</a>
			</c:if>
		</div>

		<form action="./" method="get">
			<label for="startDate">開始日:</label>
			<input type="date" name="startDate" id="startDate" value="${startDate}">
			<label for="startDate">終了日:</label>
			<input type="date" name="endDate" id="endDate" value="${endDate}">
			<button type="submit">絞り込み</button>
		</form>
		<c:if test="${ not empty loginUser }">
			<%-- ログインユーザーが存在する場合 --%>
			<div class="profile">
				<%-- そのユーザーの名前、アカウント名 --%>
				<div class="name">
					<h2>
						<c:out value="${loginUser.name}" />
					</h2>
				</div>
				<div class="account">
					@
					<c:out value="${loginUser.account}" />
				</div>
				<div class="description">
					<c:out value="${loginUser.description}" />
				</div>
				<%-- 自己紹介文 --%>
			</div>
		</c:if>
		<c:if test="${ not empty errorMessages }">
			<div class="errorMessages">
				<ul>
					<c:forEach items="${errorMessages}" var="errorMessage">
						<li><c:out value="${errorMessage}" /></li>
					</c:forEach>
				</ul>
			</div>
			<c:remove var="errorMessages" scope="session" />
		</c:if>


		<div class="form-area">
			<c:if test="${ isShowMessageForm }">
				<form action="message" method="post">
					いま、どうしてる？<br />
					<textarea name="text" cols="100" rows="5" class="tweet-box"></textarea>
					<br /> <input type="submit" value="つぶやく">（140文字まで）
				</form>
			</c:if>
		</div>

		<div class="messages">
			<c:forEach items="${messages}" var="message">
				<div class="message">
					<div class="account-name">  <%-- ユーザー名とアカウント名のセットを表示 --%>

						<span class="account"> <%-- クリック可能に --%>
						<a
							href="./?user_id=<c:out value="${message.userId}"/>"> <%-- <a>タグで囲まれ、くりっくできるようにした --%>
								<c:out value="${message.account}" />
						</a>

						</span> <span class="name"><c:out value="${message.name}" /></span>
					</div>
					<div class="text">
						<pre><c:out value="${(message.text)}"/></pre>
					</div>
					<div class="date">
						<fmt:formatDate value="${message.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" />
						<c:if test="${message.userId == loginUser.id}"> <%-- 自分の投稿だけ表示 --%>
							<form action="deleteMessage" method="post" >
								<input type="hidden" name="messageId" value="${message.id}" />
								<input type="submit" value="消去" />
							</form>
							<form action="edit" method="get" >
								<input type="hidden" name="messageId" value="${message.id}" />
								<input type="submit" value="編集" />
							</form>
						</c:if>
							<%--返信フォーム誰でも表示 --%>
							<div class="form-area">
								<c:if test="${ isShowMessageForm }">
									<form action="comment" method="post">
										<input name="messageId" value="${message.id}" type="hidden"/>
										<textarea name="text" cols="100" rows="5" class="tweet-box"></textarea>
										<br /> <input type="submit" value="返信">（140文字まで）
									</form>
								</c:if>
							</div>
							<%--コメント表示messageIdでフィルタ --%>
							<c:forEach items="${comments}" var="comment">
								<c:if test="${comment.messageId == message.id}">
									<div class="comment">
										<div class="account-name">
											<a href="./?user_id=${comment.userId}">
												<c:out value="${comment.account}" />
											</a>
										</div>
										<div class="test">
											<pre><c:out value="${comment.text}" /></pre>
										</div>
										<div class="data">
											<fmt:formatDate value="${comment.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" />
										</div>
									</div>
								</c:if>
							</c:forEach>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
	<div class="copyright">Copyright(c)iwahara.aoi</div>

</body>
</html>