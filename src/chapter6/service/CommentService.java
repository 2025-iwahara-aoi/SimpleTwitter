package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Comment;
import chapter6.beans.UserComment;
import chapter6.dao.CommentDao;
import chapter6.dao.UserCommentDao;
import chapter6.logging.InitApplication;

public class CommentService {

	/*投稿、新しいメッセージを保存する。
	 * 投稿されたメッセージ一覧を取得する。
	 * DBの切断も管理している。
	 *

	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	//Logger ログを出すために使う、処理記録を出力

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public CommentService() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Comment comment) {

		//insert()データベースに新しいデータを追加すること。

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		          " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null; //データベースとやり取りするための接続を用意

		try {
			connection = getConnection(); //DBに接続
			new CommentDao().insert(connection, comment); //投稿をDBに追加
			//MessageDaoを使い、実際のSQL実行はDAOに任せている。

			commit(connection); //成功したら反映
		} catch (RuntimeException e) {
			rollback(connection); //失敗時は、取り消し
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection); //最後は必ず、接続を閉じる。
		}
	}

	public List<UserComment> select(String userId) { //このメソッドはデータベースから取得して返すもの。

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		final int LIMIT_NUM = 1000;

		Connection connection = null; //データベースに接続するための線を宣言。
										//まだつながっていない状態。
		try {
			connection = getConnection(); //データベースと接続
			/*
			 * idをnullで初期化
			 * ServletからuserIdの値が渡ってきていたら
			 * 整数型に型変換し、idに代入
			 */

			List<UserComment> comments = new UserCommentDao().select(connection, LIMIT_NUM);
			//UserMessageDaoクラスのselect()メソッドを呼んで、データベースから投稿一覧を取得している。

			commit(connection); //今までの操作を確定保存している。

			return comments; //上で取得したList<UserMessage>を呼び出し元に返している。

		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}
}
