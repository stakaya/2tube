<?php
    define('TEMP_PATH' , '/home/temp/');
    define('DEV_KEY'   , '');
    define('DEV_CLIENT', '');

    // 文字コード
    mb_internal_encoding('UTF-8');

    // 言語設定
    mb_language('Japanese');

    // パラメータレングス
    $pos = array(14, 20, 15, 5, 5, 256, 20, 20, 6); $i = 0;

    // POST情報取得
    $stdin = '';
    if (isset($HTTP_RAW_POST_DATA)) {
        $stdin = $HTTP_RAW_POST_DATA;
    } else {
        $fp = fopen('php://input', 'r');
        if(!$fp) {
            die('ER');
        }

        while(!feof($fp)) {
            $stdin .= fgets($fp);
        }
        fclose($fp);
    }

    // ヘッダ情報取得
    $date     = substr($stdin,       0, $pos[$i]); $offset  = $pos[$i++];
    $sim      = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $term     = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $seq      = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $type     = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $email    = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $password = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $category = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $length   = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];

    // スペースを削除
    $date     = trim($date);
    $sim      = trim($sim);
    $term     = trim($term);
    $seq      = trim($seq);
    $type     = trim($type);
    $email    = trim($email);
    $password = trim($password);
    $category = trim($category);
    $length   = trim($length);
    $filename = TEMP_PATH . $date . $sim . $term . '.3gp';

    // データがある場合
    if ($length > 0) {
        $data = substr($stdin, $offset, $length);
    } else {
        $data = '';
    }

    // まだファイルに続きがある場合
    if ($seq != 'END') {

        // テンポラリファイルを作成
        $temp = @fopen($filename, 'ab');

        // ファイルオープンチェック
        if ($temp) {
            flock($temp, LOCK_EX);
            fputs($temp, $data);
            flock($temp, LOCK_UN);
            fclose($temp);
        }

        // クライアントに正常値を返却し終了
        die('OK');
    }

    // 文字コード変換
    $data = mb_convert_encoding($data, mb_internal_encoding(), 'SJIS');
    $description = $data . ' Powered by monysong.com';

    // まだファイルに続きがある場合
    if ($data != '') {
        $title    = $data;
        $keywords = $data;
    } else {
        $title    = 'Powered by monysong.com';
        $keywords = 'monysong.com';
    }

    $token = '';
    $user  = '';

    // 動画情報XML
    $xml = '<?xml version="1.0"?>'
         . '<entry xmlns="http://www.w3.org/2005/Atom" '
         .        'xmlns:media="http://search.yahoo.com/mrss/" '
         .        'xmlns:yt="http://gdata.youtube.com/schemas/2007">'
         .   '<media:group>'
         .     '<media:title type="plain">'
         .        $title
         .     '</media:title>'
         .     '<media:description type="plain">'
         .        $description
         .     '</media:description>'
         .     '<media:category scheme="http://gdata.youtube.com/schemas/2007/categories.cat">'
         .        $category
         .     '</media:category>'
         .     '<media:keywords>'
         .        $keywords
         .     '</media:keywords>'
         .   '</media:group>'
         . '</entry>';

    // URLをパース
    $url = parse_url('https://www.google.com/youtube/accounts/ClientLogin');

    // POST送信するデータを生成
    $request = "Email=$email&Passwd=$password&service=youtube&source=upload";

    // SSLリクエスト発行
    if ($fp = fsockopen('ssl://' . $url['host'], 443)) {
        $request = 'POST ' . $url['path'] . ' HTTP/1.1' . "\n"
                 . 'Content-Type: application/x-www-form-urlencoded' . "\n"
                 . 'Content-Length: ' . strlen($request) . "\n"
                 . 'Connection: Close' . "\n\n"
                 . $request;
        fputs($fp, $request);
        $temp = '';
        while (!feof($fp)) {
            $temp .= fgets($fp);
        }

        if (preg_match('/Auth=(.*?)\n/i', $temp, $match)) {
            $token = $match[1];
        }

        if (preg_match('/YouTubeUser=(.*?)\n/i', $temp, $match)) {
            $user = $match[1];
        }

        fclose($fp);
    }

    // URLをパース
    $url = parse_url('http://uploads.gdata.youtube.com/feeds/api/users/'. $user . '/uploads');

    // POST送信するデータを生成
    $temp = @fopen($filename, 'rb');
    if ($temp) {
        $data = fread($temp, filesize($filename));
        fclose($temp);
    } else {
        die('File open failed ;-(');
    }

    // 動画の部分のHTTPリクエスト
    $request = '--END_OF_PART' . "\n"
             . 'Content-Type: application/atom+xml; charset=UTF-8' . "\n\n"
             . $xml . "\n"
             . '--END_OF_PART' . "\n"
             . 'Content-Type: video/3gpp' . "\n"
             . 'Content-Transfer-Encoding: binary'. "\n\n"
             . $data . "\n"
             . '--END_OF_PART--' . "\n";

    // HTTPリクエスト発行
    if ($fp = fsockopen($url['host'], 80)) {
        $request = 'POST ' . $url['path'] . ' HTTP/1.1' . "\n"
                 . 'Host: ' . $url['host'] . "\n"
                 . 'Authorization: GoogleLogin auth=' . $token . "\n"
                 . 'GData-Version: 2' . "\n"
                 . 'X-GData-Client: ' . DEV_CLIENT . "\n"
                 . 'X-GData-Key: key=' . DEV_KEY . "\n"
                 . 'Slug: ' . $filename . "\n"
                 . 'Content-Type: multipart/related; boundary="END_OF_PART"' . "\n"
                 . 'Content-Length: ' . (strlen(bin2hex($request)) / 2) . "\n"
                 . 'Connection: close' . "\n\n"
                 . $request;

        fputs($fp, $request);
        $temp = '';
        while (!feof($fp)) {
            $temp .= fgets($fp);
        }

        fclose($fp);
        unlink($filename);
        print('OK');
        return;
    }
    print('NG');
?>
