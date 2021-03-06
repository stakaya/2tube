import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;

import javax.microedition.io.Connector;

import com.nttdocomo.io.HttpConnection;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.util.Phone;

/**
 * Communication<BR>
 * このアプリケーションで使用する通信を定義するクラスです。
 * <p>
 * @version 1.0
 * @author takaya
 * </p>
 */
public final class Communication {

    /**
     * <code>bar</code> プログレスバー
     */
    private static Progress bar = new Progress(true);

    /**
     * <code>uniq</code> 発行済み日付
     */
    private static String uniq = "";

    /**
     * 通信サイズと通信ヘッダサイズ
     * DoJa2.0は5Kバイト  mova 504i及び504iSシリーズ
     * DoJa3.0は10Kバイト mova 505i及び505iS、506iシリーズ
     * DoJa3.5は80Kバイト FOMA 900iシリーズ
     * <code>DATE_SIZE    </code> 日付サイズ
     * <code>SIM_ID_SIZE  </code> SIMIDサイズ
     * <code>TERM_ID_SIZE </code> 製造番号サイズ
     * <code>SEQ_NO_SIZE  </code> シーケンスNOサイズ
     * <code>TYPE_SIZE    </code> 識別子サイズ
     * <code>CONTRACT_SIZE</code> 契約IDサイズ
     * <code>LENGTH_SIZE  </code> データレングスサイズ
     * <code>BAR_SIZE     </code> プログレスバーの位置合わせ用
     * <code>MAIL_SIZE    </code> メールサイズ
     * <code>ALBUM_SIZE   </code> アルバムサイズ
     * <code>PASSWORD_SIZE</code> パスワードサイズ
     * <code>MAX_SEND_SIZE</code> 送信バッファサイズ
     */
    public static final int
        DATE_SIZE     =   14,
        SIM_ID_SIZE   =   20,
        TERM_ID_SIZE  =   15,
        SEQ_NO_SIZE   =    5,
        TYPE_SIZE     =    5,
        MAIL_SIZE     =  256,
        PASSWORD_SIZE =   20,
        ALBUM_SIZE    =   20,
        LENGTH_SIZE   =    6,
        BAR_SIZE      =    2,
        MAX_SEND_SIZE = 80000
                      - DATE_SIZE
                      - SIM_ID_SIZE
                      - TERM_ID_SIZE
                      - SEQ_NO_SIZE
                      - TYPE_SIZE
                      - LENGTH_SIZE
                      - MAIL_SIZE
                      - PASSWORD_SIZE
                      - ALBUM_SIZE;

    /**
     * 携帯電話のID情報
     * <code>SIM_ID </code> SIM ID
     * <code>TERM_ID</code> TERM ID
     */
    public static final String
        SIM_ID  = Phone.getProperty(Phone.USER_ID),
        TERM_ID = Phone.getProperty(Phone.TERMINAL_ID);

    /**
     * 現在日付と時間を返す
     * @return 現在日付(YYYYMMDDHHMMSS)
     */
    public static String getSystemDateTime() {
        Calendar calendar = Calendar.getInstance();
        String temp = Long.toString(calendar.get(Calendar.YEAR)  * 10000000000L
                           + (calendar.get(Calendar.MONTH) + 1)  * 100000000
                           + calendar.get(Calendar.DAY_OF_MONTH) * 1000000
                           + calendar.get(Calendar.HOUR_OF_DAY)  * 10000
                           + calendar.get(Calendar.MINUTE)       * 100
                           + calendar.get(Calendar.SECOND));

        // 同じ日付の場合は再取得する
        if (temp.equals(uniq)) {
            temp = getSystemDateTime();
        }

        return uniq = temp;
    }

    /**
     * 通信用パッディング.
     * 指定レングスに文字列を加工する。
     * レングスの後ろをスペースで埋める。
     * @param data データ
     * @param length 指定レングス
     * @return 処理結果
     */
    public static String padding(final String data, final int length) {

        // nullの場合
        if (data == null) {
            return null;
        }

        // 指定サイズより大きい場合
        if (data.length() > length) {
            return data.substring(length);
        } else if (data.length() == length) {
            return data;
        }

        // 後ろにスペースを埋める
        String temp = data;
        for (int i = data.length(); i < length; i++) {
            temp += " ";
        }

        return temp;
    }

    /**
     * 通信用文字分解.
     * 指定デリミタで文字列を分割する。
     * @param data データ
     * @param length 指定レングス
     * @return 処理結果
     */
    public static String[] split(final String data, final String delimiter) {

        // nullの場合
        if (data == null || delimiter == null) {
            return null;
        }

        int length = 0;
        String temp = data;
        while (temp.indexOf(delimiter) > 0) {
            length++;
            temp = temp.substring(temp.indexOf(delimiter) + 1);
        }

        int i = 0;
        temp = data;
        String[] result = new String[length];
        while (temp.indexOf(delimiter) > 0) {
            result[i++] = temp.substring(0, temp.indexOf(delimiter));
            temp = temp.substring(temp.indexOf(delimiter) + 1);
        }

        return result;
    }

    /**
     * データ送信
     * @param url URL
     * @param data 送信データ
     * @return 処理結果
     */
    public static synchronized String send(final String url, final byte[] data) {

        HttpConnection    conn = null;
        OutputStream      out  = null;
        InputStream       in   = null;
        InputStreamReader rec  = null;
        StringBuffer      temp = new StringBuffer();
        Dialog dialog = new Dialog(Dialog.DIALOG_ERROR, "登録エラー");

        try {
            // HTTP接続の準備
            conn = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
            conn.setRequestMethod(HttpConnection.POST);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // データを送信
            out = conn.openOutputStream();
            out.write(data);

        } catch (Exception e) {
            dialog.setText("HTTP接続例外発生\n");
            dialog.show();
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                dialog.setText("HTTPデータ送信失敗\n");
                dialog.show();
                return null;
            }
        }

        try {
            // サーバ接続
            conn.connect();
            if (conn.getResponseCode() != HttpConnection.HTTP_OK) {
                return null;
            }

            // データ受信
            in = conn.openInputStream();
            rec = new InputStreamReader(in);
            int buffer;
            while ((buffer = rec.read()) != -1) {
                temp.append((char) buffer);
            }

            // 受信結果判定
            if (!temp.toString().startsWith("OK")) {
                System.out.println("サーバエラー発生 Cord:" + temp.toString());
                System.out.println(temp.toString());
                return null;
            }
        } catch (Exception e) {
            dialog.setText("通信エラーが発生いたしました\n");
            dialog.show();
            return null;
        } finally {
            try {
                // コネクション切断
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rec != null) {
                    rec.close();
                }
            } catch (Exception e) {
                dialog.setText("通信切断に失敗しました\n");
                dialog.show();
                return null;
            }
        }
        return temp.toString();
    }

    /**
     * データ送信処理
     * @param url URL
     * @param type 識別子
     * @param comment コメント
     * @param data 送信データ
     * @return 処理結果
     */
    public static String authorize(final String url, final String mail, final String password) {

        byte[] packet = new byte[MAX_SEND_SIZE];
        String header = null;
        String result = null;
        String date = getSystemDateTime();
        ByteArrayOutputStream out = null;
        int length = 0;
        int i = 0;

        try {
            // プログレスバーの準備
            bar.reset(BAR_SIZE);
            bar.setMessage("認証中です。");
            bar.show();

            // コメントデータ送信
            header = padding(mail, MAIL_SIZE)
                   + padding(password, PASSWORD_SIZE);

            // データ編集
            byte[] sendHeader = header.getBytes();
            out = new ByteArrayOutputStream(sendHeader.length);
            out.write(sendHeader);
            out.close();
            bar.add();

            // データ送信
            result = send(url, out.toByteArray());
            if (null == result) {
                System.out.println("データ送信失敗");
                return null;
            }
        } catch (Exception e) {
            System.out.println("データ編集で例外発生");
            return null;
        }

        return result;
    }

    /**
     * データ送信処理
     * @param url URL
     * @param type 識別子
     * @param mail メール
     * @param pass パスワード
     * @param album アルバム
     * @param titel タイトル
     * @param data 送信データ
     * @return 処理結果
     */
    public static boolean
        sendData(final String url, final String type, final String mail,
                 final String pass,final String album, final String title, final InputStream data) {

        byte[] packet = new byte[MAX_SEND_SIZE];
        String header = null;
        String date = getSystemDateTime();
        ByteArrayOutputStream out = null;
        int length = 0;
        int i = 0;

        try {

            // データが無い場合
            if (data == null) {
                return false;
            }

            // プログレスバーの準備
            bar.reset(data.available() / MAX_SEND_SIZE + BAR_SIZE);
            bar.setMessage("データを送信しています。");
            bar.show();

            // cameraからのデータを送信
            while ((length = data.read(packet)) != -1) {
                header = padding(date,                     DATE_SIZE)
                       + padding(SIM_ID,                   SIM_ID_SIZE)
                       + padding(TERM_ID,                  TERM_ID_SIZE)
                       + padding(Integer.toString(++i),    SEQ_NO_SIZE)
                       + padding(type,                     TYPE_SIZE)
                       + padding(mail,                     MAIL_SIZE)
                       + padding(pass,                     PASSWORD_SIZE)
                       + padding(album,                    ALBUM_SIZE)
                       + padding(Integer.toString(length), LENGTH_SIZE);
                byte[] sendHeader = header.getBytes();
                out = new ByteArrayOutputStream(sendHeader.length + length);
                out.write(sendHeader);
                out.write(packet);
                out.close();
                bar.add();

                // データ送信
                if (null == send(url, out.toByteArray())) {
                    System.out.println("データ送信失敗");
                    return false;
                }
            }

            packet = title.getBytes();
            length = title.getBytes().length;

            // 終了データ送信
            header = padding(date,    DATE_SIZE)
                   + padding(SIM_ID,  SIM_ID_SIZE)
                   + padding(TERM_ID, TERM_ID_SIZE)
                   + padding("END",   SEQ_NO_SIZE)
                   + padding(type,    TYPE_SIZE)
                   + padding(mail,    MAIL_SIZE)
                   + padding(pass,    PASSWORD_SIZE)
                   + padding(album,   ALBUM_SIZE)
                   + padding(Integer.toString(length), LENGTH_SIZE);

            // データ編集
            byte[] sendHeader = header.getBytes();
            out = new ByteArrayOutputStream(sendHeader.length + length);
            out.write(sendHeader);
            out.write(packet);
            out.close();
            bar.add();

            // データ送信
            if (null == send(url, out.toByteArray())) {
                System.out.println("データ送信失敗");
                return false;
            }
        } catch (Exception e) {
            System.out.println("データ編集で例外発生");
            return false;
        }

        return true;
    }

    /**
     * コンストラクタ.
     * <br>アプリケーションが直接インスタンスを生成することはできません。
     */
    private Communication() {
    }
}
