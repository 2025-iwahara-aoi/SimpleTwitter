package chapter6.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chapter6.beans.User;
import chapter6.beans.UserMessage;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/index.jsp" })
public class TopServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public TopServlet() {
        InitApplication application = InitApplication.getInstance();
        application.init();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        boolean isShowMessageForm = false; //投稿フォームを表示するかどうかを表す変数
        		//初期値は、false（表示しない）にしている

        User user = (User) request.getSession().getAttribute("loginUser");
 /*							セッションから、ログインユーザーという名前のオブジェクト
   							（ログインユーザー情報を取り出す。） */

        if (user != null) {   //取り出したuserがnullでなければログイン中という意味
            isShowMessageForm = true;	//ログイン中は、投稿を表示するフラグをtrueに変更

        }
        //String userId = request.getParameter("user_id");


        List<UserMessage> messages = new MessageService().select();
        /*投稿一覧をデータベースから取り出すコード
        MessageService()というクラスを使って、select()というメゾットで、すべての投稿を
        取り出して、messagesというリストに入れる処理。*/

        request.setAttribute("messages", messages);
        //messagesという名前で投稿一覧を渡す処理。

        request.setAttribute("isShowMessageForm", isShowMessageForm);
        request.getRequestDispatcher("/top.jsp").forward(request, response);
        //("/top.jsp")をつかって画面に表示してと指示

    }
}