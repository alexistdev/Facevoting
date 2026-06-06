<?php
defined('BASEPATH') OR exit('No direct script access allowed');
use chriskacerguis\RestServer\RestController;

class Gambar extends RestController {

	public $api;
	public $upload;

	public function __construct()
	{
		parent::__construct();
		$this->load->model('Api_model', 'api');
	}


	/** Menampilkan data paslon per kategori */

	public function tambah_post(){
		$idUser = $this->post('id_user');
		$getData = $this->api->get_data_user($idUser);
		if($getData->num_rows() != 0){
			$namaGambar = substr(sha1(time()), 1, 10);
			$entryid = $getData->row()->entry_id;
			$config = array(
				'upload_path' => "gambar/user/",             //path for upload
				'allowed_types' => "jpg|png|jpeg",   //restrict extension
				'max_size' => '10000',
				'max_width' => '4200',
				'max_height' => '4200',
				'file_name' => $namaGambar
			);
			$this->load->library('upload',$config);
			if($this->upload->do_upload('upload'))
			{
				$namaURLGambar = $this->upload->data('file_name');
				/* Menyimpan ke dalam database*/
				$dataPhoto = [
					'id_album' => 3,
					'id_user' => $idUser,
					'entry_id' => $entryid,
					'nama_photo' => $namaGambar,
					'gambar' => $namaURLGambar,
					'status_train' => 2
				];
				$this->api->simpan_photo($dataPhoto);
				$data = array('upload_data' => $this->upload->data());
				$config['upload_path'].'/'.$data['upload_data']['orig_name'];


				$dataResponse = [
					'status' => 'success',
					'message' => 'Gambar berhasil diupload',
					'nama_file' => $namaURLGambar
				];
				$this->response($dataResponse, 200);
			} else {
				$dataResponse = [
					'status' => 'failed',
					'message' => strip_tags($this->upload->display_errors())
				];
				$this->set_response($dataResponse, 404);
			}
		} else {
			$dataResponse = [
				'status' => 'failed',
				'message' => 'User tidak ditemukan!'
			];
			$this->set_response($dataResponse, 404);
		}

	}

	/** Mencocokkan data */

	public function cek_post()
	{
		$idUser = $this->post('id_user');
		$getData = $this->api->get_data_user($idUser);
		if ($getData->num_rows() != 0) {
			$namaGambar = substr(sha1(time()), 1, 10);
			$config = array(
				'upload_path' => "gambar/user/",             //path for upload
				'allowed_types' => "jpg|png|jpeg",   //restrict extension
				'max_size' => '10000',
				'max_width' => '4200',
				'max_height' => '4200',
				'file_name' => $namaGambar
			);
			$this->load->library('upload',$config);
			if($this->upload->do_upload('upload')){
				$namaURLGambar = $this->upload->data('file_name');
				/* Menyimpan ke dalam database*/
				$dataPhoto = [
					'id_user' => $idUser,
					'nama_photo' => $namaGambar,
					'gambar' => $namaURLGambar
				];
				$this->api->simpan_pencocokan($dataPhoto);
				$data = array('upload_data' => $this->upload->data());
				$config['upload_path'].'/'.$data['upload_data']['orig_name'];

				/* Mendapatkan data Pembanding */
				$DataPhotoAwal = $this->api->get_data_photo($idUser);
				if($DataPhotoAwal->num_rows() > 0){
					//ada photo awal
					$photoAwal = $DataPhotoAwal->row()->gambar;
					$hasilCek = $this->_banding($photoAwal, $namaURLGambar);
					if($hasilCek != null){
						$dataResponse = [
							'status' => 'success',
							'message' => $hasilCek
						];
						$this->response($dataResponse, 200);
					} else {
						$dataResponse = [
							'status' => 'failed',
							'message' => 'Sementara tidak dapat melakukan voting!'
						];
						$this->response($dataResponse, 404);
					}

				} else {
					$dataResponse = [
						'status' => 'nopic',
						'message' => 'Anda belum melakukan perekaman!'
					];
					$this->set_response($dataResponse, 404);
				}

			} else {
				$dataResponse = [
					'status' => 'failed',
					'message' => strip_tags($this->upload->display_errors())
				];
				$this->set_response($dataResponse, 404);
			}
		} else {
			$dataResponse = [
				'status' => 'failed',
				'message' => 'User tidak ditemukan!'
			];
			$this->set_response($dataResponse, 404);
		}
	}

	private function _banding($photoAwal, $photoPembanding)
	{
		$ch = curl_init();

		curl_setopt($ch, CURLOPT_URL, 'https://www.facexapi.com/match_faces');
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_POST, 1);
		$post = array(
			'img_1' => 'http://facevoting.xyz/gambar/user/'.$photoAwal,
			'img_2' => 'http://facevoting.xyz/gambar/user/'.$photoPembanding
		);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $post);

		$headers = array();
		$headers[] = 'User_id: 603f05b94e6c5e6c15c171e7';
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

		$result = curl_exec($ch);
		if (curl_errno($ch)) {
			return null;
		}
			curl_close($ch);
			$json = json_decode($result);
			return $json->data->status;
	}
}

