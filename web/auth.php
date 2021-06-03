<?php
    // 文字コード
    mb_internal_encoding('UTF-8');

    // 言語設定
    mb_language('Japanese');

    // パラメータレングス
    $pos = array(256, 20); $i = 0;

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
    $email    = substr($stdin,       0, $pos[$i]); $offset  = $pos[$i++];
    $password = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];

    // スペースを削除
    $email    = trim($email);
    $password = trim($password);

    $token  = '';
    $user   = '';

    // URLをパース
    $url = parse_url('https://www.google.com/youtube/accounts/ClientLogin');

    // POST送信するデータを生成
    $request = "Email=$email&Passwd=$password&service=youtube&source=upload";

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

        // ユーザチェック
        if ($token != '' && $user != '') {
            print('OK');
        }
    }
?>
