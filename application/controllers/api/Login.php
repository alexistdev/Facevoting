<?php
defined('BASEPATH') OR exit('No direct script access allowed');
use chriskacerguis\RestServer\RestController;

class Login extends RestController
{


	public $api;

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Api_model', 'api');
	}

	public function otentikasi_post()
	{
		$email = $this->post('email');
		$password = sha1($this->post('password'));
		$validasi = $this->api->validasi_login($email,$password);
		if($validasi > 0){
			$getData = $this->api->cekEmail($email)->row();
			$tokenLogin = substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'),1,10);
			$sessionUser = [
				'token_login' => $tokenLogin,
				'id_user' => $getData->id_user,
				'nama' => $getData->nama,
				'identitas' =>$getData->identitas,
				'email' => $email,
				'validasi' => $getData->status
			];
			$dataTokenLogin = [
				'token_login' => $tokenLogin
			];
			$this->api->simpan_token_login($dataTokenLogin,$getData->id_user);
			$this->response($sessionUser, 200);
		} else {
			$this->response([
				'message' => 'Username atau Password yang anda masukkan salah'
			], 404);
		}
	}

	public function sudahlogin_post()
	{
		$idUser = $this->post('id_user');
		$dataUser = $this->api->get_data_user($idUser);
		if($dataUser->num_rows() > 0){
			$getData = $this->api->get_data_user($idUser)->row();
			$tokenLogin = substr(str_shuffle('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ'),1,10);
			$sessionUser = [
				'token_login' => $tokenLogin,
				'id_user' => $getData->id_user,
				'email' => $getData->email,
				'validasi' => $getData->status
			];
			$dataTokenLogin = [
				'token_login' => $tokenLogin
			];
			$this->api->simpan_token_login($dataTokenLogin,$getData->id_user);
			$this->response($sessionUser, 200);
		} else {
			$this->response([
				'message' => 'Gagal mendapatkan data'
			], 404);
		}
	}

	public function cekstatus_get()
	{
		$idUser = $this->get('id_user');
		$dataUser = $this->api->get_data_akun($idUser);
		if($dataUser->num_rows() > 0){
			$dataResponse = [
				'validasi' => $dataUser->row()->status,
				'token_login' =>$dataUser->row()->token_login,
				'nama' =>$dataUser->row()->nama,
				'identitas' =>$dataUser->row()->identitas,
				'message' => 'Berhasil mendapatkan data'
			];
			$this->response($dataResponse, 200);
		} else {
			$this->response([
				'message' => 'Gagal mendapatkan data'
			], 404);
		}
	}
}
