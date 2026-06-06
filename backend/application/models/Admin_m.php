<?php
defined('BASEPATH') or exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 * Email: belovecorp@gmail.com
 */
class Admin_m extends CI_Model
{

	public function __construct()
	{
		parent::__construct();
		$this->load->database();
		$this->tableUser = 'user';
		$this->tbadmin = 'admin';
		$this->tableDetail = 'detail_user';
		$this->tableToken = 'token';
		$this->tableAdmin = 'admin';
		$this->tableKategori = 'kategori';
		$this->tablePaslon = 'paslon';
		$this->tablePaslondet = 'detail_paslon';
		$this->tableAlbum = 'album';
		$this->tablePhoto = 'photo';
		$this->tablePemilih = 'otorisasi_pemilih';
		$this->tableJurusan = 'jurusan';
	}

	####################################################################################
	#                                  Tabel otorisasi                                #
	####################################################################################
	public function simpan_otorisasi($data){
		$this->db->insert($this->tablePemilih, $data);
	}


	####################################################################################
	#                                  Tabel album dan Photo                           #
	####################################################################################

	/** Mendapatkan data album untuk dihalaman album */
	public function get_data_album($data=null)
	{
		///$this->db->select(["count($this->tablePhoto.id_photo) as Total","$this->tableAlbum.nama_album","$this->tableDetail.nama","$this->tableAlbum.kode_album","$this->tableAlbum.id_album"]);
        $this->db->join($this->tableDetail, "$this->tableDetail.id_user = $this->tableAlbum.id_user");
//		$this->db->join($this->tablePhoto, "$this->tablePhoto.id_album = $this->tableAlbum.id_album");
		return $this->db->get($this->tableAlbum);
	}

	/** Cek Album */
	public function data_album($id=null)
	{
		$this->db->where("$this->tableAlbum.id_album", $id);
		return $this->db->get($this->tableAlbum);
	}

	/** Menyimpan data album yang diperoleh dari lambda */
	public function simpan_album($dataAlbum){
		$this->db->insert($this->tableAlbum, $dataAlbum);
	}

	/** Mendapatkan data photo */
	public function get_data_photo()
	{
		$this->db->join($this->tableAlbum, "$this->tableAlbum.id_album = $this->tablePhoto.id_album");
		$this->db->join($this->tableDetail, "$this->tableDetail.id_user = $this->tablePhoto.id_user");
		$this->db->order_by("$this->tablePhoto.status_train", "DESC");
		return $this->db->get($this->tablePhoto);
	}
	/** Mengecek data photo */
	public function data_photo($id)
	{
		$this->db->join($this->tableAlbum, "$this->tableAlbum.id_album = $this->tablePhoto.id_album");
		$this->db->where("$this->tablePhoto.id_photo", $id);
		return $this->db->get($this->tablePhoto);
	}

	/** Menghapus photo */
	public function hapus_photo($id)
	{
		$this->db->where("$this->tablePhoto.id_photo", $id);
		$this->db->delete($this->tablePhoto);
	}

	####################################################################################
	#                                  Tabel user                                      #
	####################################################################################
	/** Mendapatkan data user */
	public function get_data_user($data=null)
	{
		$this->db->join($this->tableDetail, "$this->tableDetail.id_user = $this->tableUser.id_user");
		if($data != null){
			$this->db->where("$this->tableUser.id_user", $data);
			$this->db->where("$this->tableUser.status", 2);
		}
		return $this->db->get($this->tableUser);
	}

	public function get_data_user2($data)
	{
		$this->db->where("$this->tableUser.id_user", $data);
		return $this->db->get($this->tableUser);
	}


	/** Mengaktivasi user */
	public function aktivasi_user($data,$id)
	{
		$this->db->where('id_user',$id);
		$this->db->update($this->tableUser,$data);
	}

	/** Menghapus user */
	public function hapus_user($id)
	{
		$this->db->where("$this->tableUser.id_user", $id);
		$this->db->delete($this->tableUser);
	}

	####################################################################################
	#                                  Tabel admin                                     #
	####################################################################################
	/** Dapat data untuk validasi login */
	public function validasi_login($username)
	{
		$this->db->where('username', $username);
		return $this->db->get($this->tableAdmin);
	}

	public function update_admin($data)
	{
		$this->db->where('id_admin',1);
		$this->db->update($this->tbadmin,$data);
	}

	####################################################################################
	#                                Tabel token                                       #
	####################################################################################
	/** Untuk mendapatkan data token */
	public function get_token_byId($id){
		$this->db->where('id_admin', $id);
		return $this->db->get($this->tableToken);
	}

	/** Cek Token */
	public function cek_token($username)
	{
		$this->db->join($this->tableAdmin, "$this->tableAdmin.id_admin = $this->tableToken.id_admin");
		$this->db->where("$this->tableAdmin.username", $username);
		return $this->db->get($this->tableToken)->num_rows();
	}

	/** Hapus Token saat login */
	public function hapus_token($idUser)
	{
		$this->db->where('id_admin', $idUser);
		$this->db->delete($this->tableToken);
	}

	/** Simpan data token */
	public function simpan_token($token)
	{
		$this->db->insert($this->tableToken, $token);
	}
	####################################################################################
	#                                  Tabel kategori                                  #
	####################################################################################

	/** Mendapatkan data kategori */

	public function get_data_kategori($data=NULL)
	{
		if($data != NULL){
			$this->db->where('id_kategori', $data);
		}
		return $this->db->get($this->tableKategori);

	}


	public function simpan_kategori($data)
	{
		$this->db->insert($this->tableKategori,$data);
	}

	public function update_kategori($data, $id)
	{
		$this->db->where('id_kategori',$id);
		$this->db->update($this->tableKategori,$data);
	}

	public function get_data_kategori_paslon()
	{
		$this->db->where('status_kategori' , 1);
		return $this->db->get($this->tableKategori);
	}


	####################################################################################
	#                                  Tabel paslon                                    #
	####################################################################################

	/** Menampilkan data paslon */
	public function get_data_paslon()
	{
		$this->db->join($this->tableKategori, "$this->tableKategori.id_kategori = $this->tablePaslon.id_kategori");
		$this->db->join($this->tablePaslondet, "$this->tablePaslondet.id_paslon = $this->tablePaslon.id_paslon");
		return $this->db->get($this->tablePaslon);
	}
	public function cek_paslon($id)
	{
		$this->db->where('id_paslon',$id);
		return $this->db->get($this->tablePaslon);
	}

	public function get_data_paslon2()
	{
		$this->db->join($this->tableKategori, "$this->tableKategori.id_kategori = $this->tablePaslon.id_kategori");
		$this->db->join($this->tablePaslondet, "$this->tablePaslondet.id_paslon = $this->tablePaslon.id_paslon");
		$this->db->order_by("$this->tablePaslon.perolehan","DESC");
		return $this->db->get($this->tablePaslon);
	}

	public function get_data_paslon_byKategori($data)
	{
		$this->db->where('id_kategori', $data);
		return $this->db->get($this->tablePaslon);
	}

	public function get_data_pemilih($data)
	{
		$this->db->join($this->tableDetail, "$this->tableDetail.id_user = $this->tablePemilih.id_user", "left");
		$this->db->where("$this->tablePemilih.id_kategori", $data);
		return $this->db->get($this->tablePemilih);
	}

	public function get_data_calon($data)
	{
		$this->db->select("user.id_user,detail_user.nama");
		$this->db->join("detail_user", "detail_user.id_user = user.id_user");
		$this->db->join("otorisasi_pemilih", "otorisasi_pemilih.id_user = user.id_user", "left");
		$this->db->where("user.id_user NOT IN (select otorisasi_pemilih.id_user from otorisasi_pemilih where otorisasi_pemilih.id_kategori = $data)", NULL);
	    return $this->db->get("user");
	}

	public function simpan_paslon($dataPaslon)
	{
		$this->db->insert($this->tablePaslon,$dataPaslon);
		return $this->db->insert_id();
	}

	public function simpan_detail_paslon($data)
	{
		$this->db->insert($this->tablePaslondet,$data);
	}

	public function hapus_paslon($id){
		$this->db->where("id_paslon", $id);
		$this->db->delete($this->tablePaslon);
	}




}
