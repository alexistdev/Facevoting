<?php
defined('BASEPATH') OR exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 */
class Album extends CI_Controller
{

	public $session;
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
			$data['dataAlbum']= $this->admin->get_data_album(null)->result_array();
			$view ='v_album';
			$this->_template($data,$view);
		}

	}

	/** Menampilkan detail Album yang diperoleh dari Lambda */





}
