<?php
defined('BASEPATH') OR exit('No direct script access allowed');
use chriskacerguis\RestServer\RestController;

class Kategori extends RestController {

	public $api;
	public function __construct()
	{
		parent::__construct();
		$this->load->model('Api_model', 'api');
	}

	public function tampil_get()
	{
		$idUser = $this->GET('id_user');
		$getData =  $this->api->get_data_otorisasi($idUser);
		if($getData->num_rows() !=0 ){
			$data = [
				'status' => 'berhasil',
				'result' => $getData->result_array(),
				'message' => 'Data berhasil didapatkan'
			];
			$this->response($data, 200);
		}else {
			$this->response([
				'status' => 'gagal',
				'result' => [],
				'message' => 'data kosong!'
			], 404);
		}
	}

	public function semua_get()
	{
		$getData =  $this->api->get_data_kategori_hasil();
		if($getData->num_rows() !=0 ){
			$data = [
				'status' => 'berhasil',
				'result' => $getData->result_array(),
				'message' => 'Data berhasil didapatkan'
			];
			$this->response($data, 200);
		}else {
			$this->response([
				'status' => 'gagal',
				'result' => [],
				'message' => 'data kosong!'
			], 404);
		}
	}
}
