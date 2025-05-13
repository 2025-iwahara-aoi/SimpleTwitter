package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
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

			HttpSession session = request.getSession();
			//エラーメッセージをIistという箱で用意する。何回も使えるから。
			List<String> errorMessages = new ArrayList<String>();
			String messageIdParam = request.getParameter("messageId");

			Message message = null;
				//数値だけ入ったときここに入る。
			if(!StringUtils.isBlank(messageIdParam) && messageIdParam.matches("[0-9]+$") ){
				//int型に変換できるのはmessageIdParam変数の中身が数字だけで構成されていると
				//if条件分岐で担保されているから。
				int messageId = Integer.parseInt(messageIdParam);
				message = new MessageService().select(messageId);

			}
			if(message == null) {
				errorMessages.add("不正なパラメーターが入力されました");
				session.setAttribute("errorMessages", errorMessages);
				response.sendRedirect("./");
				return;
			}else {
				request.setAttribute("message", message);
				request.getRequestDispatcher("/edit.jsp").forward(request, response);
			}

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
		            new MessageService().update(message);

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

