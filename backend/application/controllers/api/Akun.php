<?php
defined('BASEPATH') OR exit('No direct script access allowed');
use chriskacerguis\RestServer\RestController;

class Akun extends RestController {
	public $api;

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Api_model', 'api');
	}

	public function tampil_get()
	{
		$idUser = $this->GET('id_user');
		$getAkun =  $this->api->get_data_akun($idUser);
		if($getAkun->num_rows() != 0){
			foreach($getAkun->result_array() as $row){
				$data['email'] =$row['email'];
				$data['nama'] =$row['nama'];
				$data['identitas'] =$row['identitas'];
			};
			$this->response($data, 200);
		} else {
			$this->response([
				'status' => 'gagal',
				'message' => 'data kosong!'
			], 404);
		}
	}

	public function tampil_put($idUser)
	{
		$nama = $this->put('nama');
		$identitas = $this->put('identitas');
		$password = $this->put('password');
		$getAkun =  $this->api->get_data_akun($idUser);
		if($getAkun->num_rows() !=0 ){
			if(!empty($password)){
				$dataPassword = [
					'password' => sha1($password)
				];
				$this->api->update_password($dataPassword,$idUser);
				$dataDetail = [
					'nama' => $nama,
					'identitas' =>$identitas
				];
			} else {
				$dataDetail = [
					'nama' => $nama,
					'identitas' =>$identitas
				];
			}

			$update = $this->api->update_detail_user($dataDetail,$idUser);
			if ($update) {
				$data = [
					'status' => 'berhasil',
					'message' => 'Data berhasil diperbaharui'
				];
				$this->response($data, 200);
			}else{
				$this->response([
					'status' => 'gagal',
					'message' => 'Gagal menyimpan ke dalam server!'
				], 404);
			}
		} else {
			$this->response([
				'status' => 'gagal',
				'message' => 'Silahkan relogin!'
			], 404);
		}
	}
}
