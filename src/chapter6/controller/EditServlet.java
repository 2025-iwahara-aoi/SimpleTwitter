package chapter6.controller;

import static chapter6.utils.DBUtil.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.exception.NoRowsUpdatedRuntimeException;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


		/**
		* ロガーインスタンスの生成
		*/
		Logger log = Logger.getLogger("twitter");

		/**
		* デフォルトコンストラクタ
		* アプリケーションの初期化を実施する。
		*/
		public EditServlet() {
			InitApplication application = InitApplication.getInstance();
			application.init();

		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {

			log.info(new Object(){}.getClass().getEnclosingClass().getName() +
			        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

			String messageIdParam = request.getParameter("messageId");
			//Idがない場合。
			if(messageIdParam == null || messageIdParam.isEmpty()) {
				request.setAttribute("errorParam", "不正なパラメーターが入力されました");
				request.getRequestDispatcher("/top.jsp").forward(request, response);
				return;
			}

			int messageId;

			//try-catchで存在しているのが数字じゃない場合の処理。
			try {
				messageId = Integer.parseInt(messageIdParam);
			// messageIdが数値でない場合の処理
			}catch(NumberFormatException e){
				request.setAttribute("errorParam", "不正なパラメーターが入力されました");
				request.getRequestDispatcher("/top.jsp").forward(request, response);
				return;
			}
				//ここでデータベースと接続して、有効なIdか確かめて出力させる。
				Connection connection = getConnection();
				UserMessage message = new MessageService().getMessage(connection,messageId);

				if(message == null) {
					request.setAttribute("errorParam", "不正なパラメーターが入力されました");
					request.getRequestDispatcher("/top.jsp").forward(request, response);
					return;
				}
				// 取得した内容を JSP に渡す
				request.setAttribute("message", message);
				// 編集ページへ転送
				request.getRequestDispatcher("/edit.jsp").forward(request, response);
			}

		@Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws IOException, ServletException {

			log.info(new Object(){}.getClass().getEnclosingClass().getName() +
					" : " + new Object(){}.getClass().getEnclosingMethod().getName());

			List<String> errorMessages = new ArrayList<>();

			Message message = getMessage(request);

			if (isValid(message, errorMessages)) {
				try {
					Connection connection = getConnection();
		            new MessageService().updateMessage(connection, message.getId(), message.getText());

				} catch (NoRowsUpdatedRuntimeException e) {
					log.warning("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
					errorMessages.add("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
				}
			}
			//エラーがあったら、エラーメッセージを持って戻る。
			if (errorMessages.size() != 0) {
				request.setAttribute("errorMessages", errorMessages);
				request.setAttribute("message", message);
				request.getRequestDispatcher("edit.jsp").forward(request, response);
				return;
			}

			response.sendRedirect("./");
		}
			private Message getMessage(HttpServletRequest request) throws IOException, ServletException {

				 log.info(new Object(){}.getClass().getEnclosingClass().getName() +
					        " : " + new Object(){}.getClass().getEnclosingMethod().getName());
				 //空のユーザーの箱をuserに入れている
				Message message = new Message();
				message.setId(Integer.parseInt(request.getParameter("messageId")));
				message.setText(request.getParameter("text"));

				//userを返している
				return message;
			}

			private boolean isValid(Message message, List<String> errorMessages) {

				log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

				String text = message.getText();

				if (StringUtils.isBlank(text)) {
		            errorMessages.add("メッセージを入力してください");
		        } else if (140 < text.length()) {
		            errorMessages.add("140文字以下で入力してください");
				}
				if (errorMessages.size() != 0) {
					return false;
				}
				return true;

	    }
}

