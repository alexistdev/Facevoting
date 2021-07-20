<?php
defined('BASEPATH') OR exit('No direct script access allowed');
use chriskacerguis\RestServer\RestController;

class Suara extends RestController {
	public $api;

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Api_model', 'api');
	}

	public function vote_post()
	{
		$idUser = $this->post('id_user');
		$idKategori = $this->post('id_kategori');
		$idPaslon = $this->post('id_paslon');

		//mendapatkan data suara
		$getPerolehan = $this->api->get_data_paslon2($idPaslon)->row()->perolehan;
		$getPerolehan += 1;

		//masuk ke tabel paslon
		$dataPaslon = [
			'perolehan' => $getPerolehan
		];
		$this->api->perbaharui_paslon($dataPaslon,$idPaslon);

		//masuk ke tabel voting
		$dataVoting = [
			'id_user' => $idUser,
			'id_kategori' => $idKategori,
			'id_paslon' => $idPaslon,
			'tanggal_voting' => date('Y-m-d H:i:s')
		];
		$this->api->simpan_voting($dataVoting);

		//masuk ke tabel otorisasi_pemilih
		$dataOt = [
			'status' => 2
		];
		$this->api->perbaharui_data_otorisasi($dataOt,$idUser);
		$dataResponse = [
			'status' => 'berhasil',
			'message' => 'Berhasil Voting'
		];
		$this->response($dataResponse, 200);
	}

	public function tampil_get($idUser)
	{
		$getAkun =  $this->api->get_data_voting($idUser);
		if($getAkun->num_rows() != 0){
			$data = [
				'status' => 'berhasil',
				'result' => $getAkun ->result_array(),
				'message' => 'Data berhasil didapatkan'
			];
			$this->response($data, 200);
		} else {
			$this->response([
				'status' => 'gagal',
				'message' => 'Silahkan relogin!'
			], 404);
		}
	}

	public function perolehan_get()
	{
		$Kategori = $this->get('id_kategori');
		$dataKategori = $this->api->get_data_perolehan($Kategori);
		$data = [
			'status' => 'berhasil',
			'result' => $dataKategori ->result_array(),
			'message' => 'Data berhasil didapatkan'
		];
		$this->response($data, 200);
	}
}
