package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class MessageDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public MessageDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Connection connection, Message message) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO messages ( ");
			sql.append("    user_id, ");
			sql.append("    text, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, "); // user_id
			sql.append("    ?, "); // text
			sql.append("    CURRENT_TIMESTAMP, "); // created_date
			sql.append("    CURRENT_TIMESTAMP "); // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());

			ps.setInt(1, message.getUserId());
			ps.setString(2, message.getText());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	public void deleteMessage(Connection connection, int messageId) {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			String sql = "DELETE FROM messages WHERE id = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	public void updateMessage(Connection connection, int messageId, String newText) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			String sql = "UPDATE messages SET text = ?, updated_date = CURRENT_TIMESTAMP WHERE id = ?";
			ps = connection.prepareStatement(sql);
			ps.setString(1, newText);
			ps.setInt(2, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	public UserMessage getMessage(Connection connection, int messageId) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT messages.id, messages.user_id, messages.text, messages.created_date, " +
					"users.name, users.account " +
					"FROM messages " +
					"LEFT JOIN users ON messages.user_id = users.id " +
					"WHERE messages.id = ?";

			ps = connection.prepareStatement(sql);
			ps.setInt(1, messageId);
			rs = ps.executeQuery();

			if (rs.next()) {
				UserMessage message = new UserMessage();
				message.setId(rs.getInt("id"));
				message.setUserId(rs.getInt("user_id"));
				message.setText(rs.getString("text"));
				message.setCreatedDate(rs.getTimestamp("created_date"));
				message.setName(rs.getString("name"));
				message.setAccount(rs.getString("account"));
				return message;
			} else {
				return null; // 見つからない場合
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(rs);
			close(ps);
		}
	}

}