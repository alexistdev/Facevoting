<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 */
class Testing extends CI_Controller
{
	public function index()

	{
		$curl = curl_init();

		curl_setopt_array($curl, [
			CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album?album=6ed566063d&albumkey=e9909eeb68a9aca662b8ee5b23ae6995ae7bb5b27ddc841ea38936c5ea4b4140",
			CURLOPT_RETURNTRANSFER => true,
			CURLOPT_FOLLOWLOCATION => true,
			CURLOPT_ENCODING => "",
			CURLOPT_MAXREDIRS => 10,
			CURLOPT_TIMEOUT => 30,
			CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
			CURLOPT_CUSTOMREQUEST => "GET",
			CURLOPT_HTTPHEADER => [
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
	}

	public function rebuild()
	{
		$curl = curl_init();

		curl_setopt_array($curl, [
			CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album_rebuild?album=6ed566063d&albumkey=e9909eeb68a9aca662b8ee5b23ae6995ae7bb5b27ddc841ea38936c5ea4b4140",
			CURLOPT_RETURNTRANSFER => true,
			CURLOPT_FOLLOWLOCATION => true,
			CURLOPT_ENCODING => "",
			CURLOPT_MAXREDIRS => 10,
			CURLOPT_TIMEOUT => 30,
			CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
			CURLOPT_CUSTOMREQUEST => "GET",
			CURLOPT_HTTPHEADER => [
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
	}

	public function demo()
	{
		$curl = curl_init();

		curl_setopt_array($curl, [
			CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album?album=CELEBS&albumkey=b1ccb6caa8cefb7347d0cfb65146d5e3f84608f6ee55b1c90d37ed4ecca9b273",
			CURLOPT_RETURNTRANSFER => true,
			CURLOPT_FOLLOWLOCATION => true,
			CURLOPT_ENCODING => "",
			CURLOPT_MAXREDIRS => 10,
			CURLOPT_TIMEOUT => 30,
			CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
			CURLOPT_CUSTOMREQUEST => "GET",
			CURLOPT_HTTPHEADER => [
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
	}
	public function viewAlbum()
	{
		$curl = curl_init();

		curl_setopt_array($curl, [
			CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album?album=6ed566063d&albumkey=e9909eeb68a9aca662b8ee5b23ae6995ae7bb5b27ddc841ea38936c5ea4b4140",
			CURLOPT_RETURNTRANSFER => true,
			CURLOPT_FOLLOWLOCATION => true,
			CURLOPT_ENCODING => "",
			CURLOPT_MAXREDIRS => 10,
			CURLOPT_TIMEOUT => 30,
			CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
			CURLOPT_CUSTOMREQUEST => "GET",
			CURLOPT_HTTPHEADER => [
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
	}

	public function buatAlbum()
	{
		$namaAlbum = "Facevoting2021";
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
			CURLOPT_POSTFIELDS => "album=$namaAlbum",
			CURLOPT_HTTPHEADER => [
				"content-type: application/x-www-form-urlencoded",
				"x-rapidapi-host: lambda-face-recognition.p.rapidapi.com",
				"x-rapidapi-key: 932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974"
			],
		]);
		$response = curl_exec($curl);
		$err = curl_error($curl);
		if ($err) {
			return null;
		} else {
			//$json = json_decode($response);
			//return $json->albumkey;
			echo $response;
		}
	}

	public function banding()
	{
		$ch = curl_init();

		curl_setopt($ch, CURLOPT_URL, 'https://www.facexapi.com/match_faces');
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_POST, 1);
		$post = array(
			'img_1' => 'http://facevoting.xyz/gambar/user/ca708560de.png',
			'img_2' => 'http://facevoting.xyz/gambar/user/609584f73b.png'
		);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $post);

		$headers = array();
		$headers[] = 'User_id: 6027d1350e555803ec2a44c7';
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

		$result = curl_exec($ch);
		if (curl_errno($ch)) {
			echo 'Error:' . curl_error($ch);
		}
		curl_close($ch);
		echo $result;

	}

}
