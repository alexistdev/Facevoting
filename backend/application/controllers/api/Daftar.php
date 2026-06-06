<?php
defined('BASEPATH') OR exit('No direct script access allowed');
use chriskacerguis\RestServer\RestController;

class Daftar extends RestController
{

	public $api;

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Api_model', 'api');
	}

	public function tambah_post()
	{
		$nama = $this->post('nama');
		$identitas = $this->post('identitas');
		$email = $this->post('email');
		$password = $this->post('password');
		$token = $this->post('token_firebase');
		$tokenLogin = substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'),1,10);

		$passwordKonversi = sha1($password);
		$cekEmail = $this->api->cekEmail($email)->num_rows();
		if($cekEmail != 0){
			$dataRespon = [
				'status' => 'Failed',
				'message' => 'Email sudah terdaftar, silahkan gunakan email yang lain!'
			];
			$this->response($dataRespon, 404);
		} else {
			$tabelUser = [
				'email' => $email,
				'password' => $passwordKonversi,
				'token_firebase' => $token,
				'token_login' => $tokenLogin,
				'entry_id' => substr(sha1(time()), 1, 10),
				'status' => 2
			];
			$id = $this->api->simpan_data_user($tabelUser);
			$tableDetail = [
				'id_user' => $id,
				'nama' => $nama,
				'identitas' => $identitas
			];
			$this->api->simpan_data_detail($tableDetail);
			$dataRespon = [
				'status' => 'Success',
				'message' => 'Data User berhasil disimpan!',
				'id_user' => $id,
				'validasi' => 2,
				'token_login' => $tokenLogin
			];
			$this->response($dataRespon, 200);
		}

	}
}
