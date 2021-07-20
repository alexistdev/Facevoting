<?php

$curl = curl_init();

curl_setopt_array($curl, [
    CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_FOLLOWLOCATION => true,
    CURLOPT_ENCODING => "",
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 30,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => "POST",
    CURLOPT_POSTFIELDS => "album=user",
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
/*
{
    album: "user",
msg: "Please put this in a safe place and remember it, you'll need it!",
albumkey: "21dc87b9abe643901bfc15c052fe857ac9cae5d44bc48511378a409709ece009"
}
*/