package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.User;
import chapter6.dao.UserDao;
import chapter6.logging.InitApplication;
import chapter6.utils.CipherUtil;

public class UserService {

	
	//DAOを使ってデータベースとのやり取りを担っている
	//パスワードの暗号化処理
	//トランザクション制御

    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public UserService() {
        InitApplication application = InitApplication.getInstance();
        application.init();

        //サービスクラスのインスタンスが作成されるとき、アプリケーションの
        //初期化、（ログ設定など）一度だけ行う。
    }

    public void insert(User user) {


	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        Connection connection = null;
        try {
            // パスワード暗号化
            String encPassword = CipherUtil.encrypt(user.getPassword());
            user.setPassword(encPassword);

            connection = getConnection();
            new UserDao().insert(connection, user);
            
            //DBコネクションを取得し、DAOでインサートを実行
            
            commit(connection);  //正常時コミット。
        } catch (RuntimeException e) {
            rollback(connection);  //異常時ロールバック。
            
            
		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw e;
        } catch (Error e) {
            rollback(connection);
		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw e;
        } finally {
            close(connection);   //最後にクラス。
        }
    }
    
    public User select(String accountOrEmail, String password) {
    	
    	//ログインの認証。パスワードを暗号化。
    	
  	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
          " : " + new Object(){}.getClass().getEnclosingMethod().getName());

          Connection connection = null;
          try {
              // パスワード暗号化
              String encPassword = CipherUtil.encrypt(password);

              connection = getConnection();
              User user = new UserDao().select(connection, accountOrEmail, encPassword);
              commit(connection);
              
              //結果があれば、Userを返却

              return user;
          } catch (RuntimeException e) {
              rollback(connection);
  		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
              throw e;
          } catch (Error e) {
              rollback(connection);
  		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
              throw e;
          } finally {
              close(connection);
          }
      }

    public User select(int userId) {
    	
    	//ユーザーのIDからユーザー情報を取得するメソッド
    	//ログイン後プロフィール画面などで利用されるケースが多い

        log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        Connection connection = null;
        try {
            connection = getConnection();
            User user = new UserDao().select(connection, userId);
            commit(connection);

            return user;
        } catch (RuntimeException e) {
            rollback(connection);
    	  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw e;
        } catch (Error e) {
            rollback(connection);
    	  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw e;
        } finally {
            close(connection);
        }
    }
    
    public void update(User user) {
    	//パスワードを再暗号化　ここをいじれば変更せず送れる。

        log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        Connection connection = null;
        try {
            connection = getConnection();
            
            
            
            

            // パスワードが空か null の場合は、現在のパスワードをそのまま使う
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                User existingUser = new UserDao().select(connection, user.getId());
                user.setPassword(existingUser.getPassword());
            } else {
                // 入力がある場合のみ暗号化して更新
                String encPassword = CipherUtil.encrypt(user.getPassword());
                user.setPassword(encPassword);
            }
            
            

            new UserDao().update(connection, user);
            commit(connection);
        } catch (RuntimeException e) {
            rollback(connection);
    	  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw e;
        } catch (Error e) {
            rollback(connection);
    	  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw e;
        } finally {
            close(connection);
        }
    }
}