package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.UserMessage;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class UserMessageDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public UserMessageDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public List<UserMessage> select(Connection connection, Integer userId, String startDate, String endDate, int num) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		/*これは、安全にSQLを送るための特別なオブジェクト?。
		 	psという名前で、その変数を用意している？*/

		try {
			StringBuilder sql = new StringBuilder();
			//一つの文字列を何度も追加していくもの。
			sql.append("SELECT ");
			sql.append("    messages.id as id, ");
			sql.append("    messages.text as text, ");
			sql.append("    messages.user_id as user_id, ");
			sql.append("    users.account as account, ");
			sql.append("    users.name as name, ");
			sql.append("    messages.created_date as created_date ");
			sql.append("FROM messages ");
			sql.append("INNER JOIN users ");
			//テーブルを結合して、messageとuserをセットで取得できるようなる
			sql.append("ON messages.user_id = users.id ");
			sql.append("WHERE messages.created_date BETWEEN ? AND ?  ");

			if (userId != null) { //	userIdが渡されている時だけ
				sql.append("AND messages.user_id = ? ");
			}
			sql.append("ORDER BY created_date DESC limit " + num);

			ps = connection.prepareStatement(sql.toString());

			ps.setString(1, startDate);
			ps.setString(2, endDate);

			if (userId != null) {
				ps.setInt(3, userId);
			}

			ResultSet rs = ps.executeQuery();
			/*上で準備した、psを実行する。
			 * 実行結果をResultSetに代入する。
			 * ResultSet（rs）:rsを使って1行ずつ取り出せる。*/
			List<UserMessage> messages = toUserMessages(rs);

			/*rsに入っているデータベース検索結果を、
			 * toUserMessages（rs）というメソッドで、*/
			return messages;
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	private List<UserMessage> toUserMessages(ResultSet rs) throws SQLException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		List<UserMessage> messages = new ArrayList<UserMessage>();
		try {
			while (rs.next()) {
				UserMessage message = new UserMessage();
				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setUserId(rs.getInt("user_id"));
				message.setAccount(rs.getString("account"));
				message.setName(rs.getString("name"));
				message.setCreatedDate(rs.getTimestamp("created_date"));

				messages.add(message);
			}
			return messages;
		} finally {
			close(rs);
		}
	}
}