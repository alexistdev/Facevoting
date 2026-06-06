<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Daftar extends CI_Controller {


	public function __construct()
	{
		parent::__construct();
		//$this->load->model('M_inbox', 'inbox');
	}

	public function index()
	{
		$idAlbum = $this->buat_album();
		//$this->load->view('v_daftar');
		echo $idAlbum->albumkey;
	}

	private function buat_album()
	{
		$curl = curl_init();
		$albumku = "amanda17";
		curl_setopt_array($curl, [
			CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album",
			CURLOPT_RETURNTRANSFER => true,
			CURLOPT_FOLLOWLOCATION => true,
			CURLOPT_ENCODING => "",
			CURLOPT_MAXREDIRS => 10,
			CURLOPT_TIMEOUT => 30,
			CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
			CURLOPT_CUSTOMREQUEST => "POST",
			CURLOPT_POSTFIELDS => "album=$albumku",
			CURLOPT_HTTPHEADER => [
				"content-type: application/x-www-form-urlencoded",
				"x-rapidapi-host: lambda-face-recognition.p.rapidapi.com",
				"x-rapidapi-key: 932571abf0msh45cf0f3cef74aacp19e151jsn33e9949a1974"
			],
		]);
		$response = curl_exec($curl);
		$err = curl_error($curl);
		if ($err) {
			$json = json_decode($err);
			return $json;
		} else {
			$json = json_decode($response);
			return $json;
		}
	}
}
