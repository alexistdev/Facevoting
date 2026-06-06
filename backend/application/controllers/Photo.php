<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 */
class Photo extends CI_Controller
{

	public $input;
	public $load;
	public $session;
	public $form_validation;
	public $admin;
	public $tokenSession;
	public $tokenServer;

	/** Constructor dari Class Login */
	public function __construct()
	{
		parent::__construct();
		$this->load->model('Admin_m', 'admin');
		if ($this->session->userdata('is_login_in') !== TRUE) {
			redirect('login');
		}
		$this->idUser = $this->session->userdata('id_admin');
		$this->tokenSession = $this->session->userdata('token');
		$this->tokenServer = $this->admin->get_token_byId($this->idUser)->row()->token;

	}

	/** Template untuk memanggil view */
	private function _template($data, $view)
	{
		$this->load->view('view/' . $view, $data);
	}

	/** Menampilkan halaman index photo */
	public function index()

	{
		if($this->tokenSession != $this->tokenServer){
			_unlogin();
		} else {
			$data['title'] = "Dashboard | FaceVoting Versi 1.0";
			$data['dataPhoto']= $this->admin->get_data_photo()->result_array();
			$view ='v_photo';
			$this->_template($data,$view);
		}

	}
	/** Menampilkan detail Album yang diperoleh dari Lambda */
	public function rebuild()
	{
		$id=3;
		$dataAlbum = $this->admin->data_album($id);
		$namaAlbum = $dataAlbum->row()->nama_album;
		$albumKey = $dataAlbum->row()->kode_album;
		$data['title'] = "Dashboard | FaceVoting Versi 1.0";
		$data['getRebuild']= $this->_prosesRebuild($namaAlbum,$albumKey);
		$view ='v_rebuildalbum';
		$this->_template($data,$view);
	}
	private function _prosesRebuild($namaAlbum,$albumKey){
		$curl = curl_init();

		curl_setopt_array($curl, [
			CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album_rebuild?album=".$namaAlbum."&albumkey=".$albumKey,
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
//			echo "cURL Error #:" . $err;
			return null;
		} else {
			return $response;
		}
	}
	/** Khusus album */
	public function viewAlbum()
	{
		$id = 3;
		$dataAlbum = $this->admin->data_album($id);
		$namaAlbum = $dataAlbum->row()->nama_album;
		$albumKey = $dataAlbum->row()->kode_album;
		$data['title'] = "Dashboard | FaceVoting Versi 1.0";
		$data['getViewAlbum']= $this->_cekAlbum($namaAlbum,$albumKey);
		$view ='v_detailalbum';
		$this->_template($data,$view);

	}


	private function _cekAlbum($namaAlbum,$albumKey)
	{
		$curl = curl_init();

		curl_setopt_array($curl, [
			CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album?album=".$namaAlbum."&albumkey=".$albumKey,
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
	//			echo "cURL Error #:" . $err;
			return null;
		} else {
			return $response;
		}
	}

	public function rekam($idx=null)
	{
		$id=$idx;
		if($idx == null || $idx == ''){
			redirect('Photo');
		} else {
			$cekIdPhoto = $this->admin->data_photo($id)->num_rows();
			if($cekIdPhoto == 0){
				redirect('Photo');
			}else {
				//get data photo
				$getData = $this->admin->data_photo($id)->row();
				$urlGambar = $getData->gambar;
				$namaAlbum = $getData->nama_album;
				$myAlbumKey = $getData->kode_album;
				$entryID = $getData->entry_id;
				/* train ke lambda */
				$ResultTrain = $this->train_album($urlGambar,$namaAlbum,$myAlbumKey,$entryID);
				if($ResultTrain == null){
					$this->session->set_flashdata('pesan', '<div class="alert alert-danger" role="alert">Perekaman data Gagal!</div>');
					redirect('Photo');
				} else {
					$tampilPesan = json_decode($ResultTrain);
					$this->session->set_flashdata('pesan', '<div class="alert alert-success" role="alert">Berhasil, jumlah photo tersimpan ke dalam album <span class="text-warning"> '.$tampilPesan->album.'</span> adalah <span class="text-warning">'.$tampilPesan->image_count.'</span> gambar </div>');
					redirect('Photo');
				}
			}
		}
	}

	/** Menghapus photo member */
	public function hapus($idx=null){
		$id=$idx;
		if($idx == null || $idx == ''){
			redirect('Photo');
		} else {
			$cekIdPhoto = $this->admin->data_photo($id)->num_rows();
			if($cekIdPhoto == 0){
				redirect('Photo');
			}else {
				$namaPhoto = $this->admin->data_photo($id)->row()->gambar;
				$this->admin->hapus_photo($id);
				unlink( FCPATH . "gambar/user/" . $namaPhoto );
				$this->session->set_flashdata('pesan', '<div class="alert alert-danger" role="alert">Photo berhasil dihapus!</div>');
				redirect('Photo');
			}
		}
	}

	/** Train */
	private function train_album($namaURLGambar,$namaAlbum,$myAlbumKey,$entryID)
	{
		$curl = curl_init();
		curl_setopt_array($curl, [
			CURLOPT_URL => "https://lambda-face-recognition.p.rapidapi.com/album_train",
			CURLOPT_RETURNTRANSFER => true,
			CURLOPT_FOLLOWLOCATION => true,
			CURLOPT_ENCODING => "",
			CURLOPT_MAXREDIRS => 10,
			CURLOPT_TIMEOUT => 30,
			CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
			CURLOPT_CUSTOMREQUEST => "POST",
			CURLOPT_POSTFIELDS => "urls=http%3A%2F%2Ffacevoting.xyz%2Fgambar%2Fuser%2F".$namaURLGambar. "&album=".$namaAlbum."&albumkey=".$myAlbumKey."&entryid=".$entryID,
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
//			echo "cURL Error #:" . $err;
			return null;
		} else {
			return $response;
		}
	}
}
