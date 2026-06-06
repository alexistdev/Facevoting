<?php
defined('BASEPATH') OR exit('No direct script access allowed');
use chriskacerguis\RestServer\RestController;

class Paslon extends RestController {

	/**
	 * Index Page for this controller.
	 *
	 * Maps to the following URL
	 * 		http://example.com/index.php/welcome
	 *	- or -
	 * 		http://example.com/index.php/welcome/index
	 *	- or -
	 * Since this controller is set as the default controller in
	 * config/routes.php, it's displayed at http://example.com/
	 *
	 * So any other public methods not prefixed with an underscore will
	 * map to /index.php/welcome/<method_name>
	 * @see https://codeigniter.com/user_guide/general/urls.html
	 *
	 *
	 */

	public $api;

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Api_model', 'api');
	}


	/** Menampilkan data paslon per kategori */
	public function tampil_post()
	{
		$idKategori = $this->post('id_kategori');
		$getData =  $this->api->get_data_paslon($idKategori);
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

	/** Menampilkan detail paslon */
	public function detail_post()
	{
		$idPaslon = $this->post('id_paslon');
		$getData =  $this->api->get_detail_paslon($idPaslon);
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

