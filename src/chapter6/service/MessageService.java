package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.dao.MessageDao;
import chapter6.dao.UserMessageDao;
import chapter6.logging.InitApplication;

public class MessageService {

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
	public MessageService() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Message message) {

		//insert()データベースに新しいデータを追加すること。

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		          " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null; //データベースとやり取りするための接続を用意

		try {
			connection = getConnection(); //DBに接続
			new MessageDao().insert(connection, message); //投稿をDBに追加
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

	public void delete(Connection connection, int messageId) {

		//insert()データベースに新しいデータを追加すること。

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		          " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		try {
			connection = getConnection(); //DBに接続
			new MessageDao().delete(connection, messageId); //投稿をDBに追加
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

	public void update(Message message) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		          " : " + new Object(){}.getClass().getEnclosingMethod().getName());
		Connection connection = getConnection(); //DBに接続

		try {
			new MessageDao().update(connection,message); //投稿をDBに追加
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


	/*
	 * selectの引数にString型のuserIdを追加
	 */
	public List<UserMessage> select(String userId, String startDate, String endDate) { //このメソッドはデータベースから取得して返すもの。

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		final int LIMIT_NUM = 1000;
		Connection connection = null; //データベースに接続するための線を宣言。
										//まだつながっていない状態。
		// 現在日時を取得
		Date nowDate = new Date();
		try {
			connection = getConnection(); //データベースと接続
			/*
			 * idをnullで初期化
			 * ServletからuserIdの値が渡ってきていたら
			 * 整数型に型変換し、idに代入
			 */
			Integer id = null;
			if (!StringUtils.isEmpty(userId)) {
				id = Integer.parseInt(userId);
			}

			if (!StringUtils.isBlank(startDate)) {
				startDate = (startDate + " 00:00:00");
			}else {
				startDate = "2020-01-01 00:00:00";
			}
			if (!StringUtils.isBlank(endDate)) {
				endDate = (endDate+ " 23:59:59");
			}else {
				// 表示形式を指定
				SimpleDateFormat sdf1
				= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
				endDate = sdf1.format(nowDate);
			}

			List<UserMessage> messages = new UserMessageDao().select(connection, id, startDate, endDate, LIMIT_NUM);
			//UserMessageDaoクラスのselect()メソッドを呼んで、データベースから投稿一覧を取得している。

			commit(connection); //今までの操作を確定保存している。

			return messages; //上で取得したList<UserMessage>を呼び出し元に返している。

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

	public Message select(int messageId) {

		//insert()データベースに新しいデータを追加すること。

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		          " : " + new Object(){}.getClass().getEnclosingMethod().getName());
			Connection connection = getConnection();
		try {
			Message message = new MessageDao().select(connection,messageId); //投稿をDBに追加
			//MessageDaoを使い、実際のSQL実行はDAOに任せている。

			commit(connection); //成功したら反映
			return message;     // 呼び出し元に返す

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

}
