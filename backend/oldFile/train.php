<?php

$curl = curl_init();
curl_setopt_array($curl, [
    CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album_train",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_ENCODING => "",
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 30,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => "POST",
    CURLOPT_POSTFIELDS => "urls=http%3A%2F%2Ffacevoting.xyz%2F614c5a713b.jpg&album=6ed566063d&albumkey=e9909eeb68a9aca662b8ee5b23ae6995ae7bb5b27ddc841ea38936c5ea4b4140&entryid=ae7bb5b27ddc8",
    CURLOPT_HTTPHEADER => [
        "content-type: application/x-www-form-urlencoded",
        "x-rapidapi-host: lambda-face-recognition.p.rapidapi.com",
        "x-rapidapi-key: 932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974"
    ],
]);

$response = curl_exec($curl);
$err = curl_error($curl);

curl_close($curl);

if ($err) {
    echo "cURL Error #:" . $err;
} else {
    echo $response;
}
