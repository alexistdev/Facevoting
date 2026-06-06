<?php
defined('BASEPATH') or exit('No direct script access allowed');
/**
 * FaceVoting REST API
 * Author: Berkat Fa'atulo Halawa
 * Email: belovecorp@gmail.com
 */
class Api_model extends CI_Model
{

	public function __construct()
	{
		parent::__construct();
		$this->load->database();
		$this->tableKategori = 'kategori';
		$this->tablePaslon = 'paslon';
		$this->tableDetailPaslon = 'detail_paslon';
		$this->tableUser = 'user';
		$this->tableDetailUser = 'detail_user';
		$this->tableAlbum = 'album';
		$this->tablePhoto = 'photo';
		$this->tablePencocokan = 'pencocokan';
		$this->tableOtorisasi = 'otorisasi_pemilih';
		$this->tableVoting = 'voting';
	}
	/** *******************************************************************************
	 * Tabel Voting
	 *  *******************************************************************************
	 */
	public function simpan_voting($data)
	{
		$this->db->insert($this->tableVoting,$data);
	}

	public function get_data_voting($idUser)
	{
		$this->db->join($this->tableDetailUser, "$this->tableDetailUser.id_user=$this->tableVoting.id_user");
		$this->db->join($this->tableKategori, "$this->tableKategori.id_kategori=$this->tableVoting.id_kategori");
		$this->db->where("$this->tableVoting.id_user", $idUser);
		return $this->db->get($this->tableVoting);
	}

	public function get_data_perolehan ($idKategori)
	{
		//$this->db->select("$this->tablePaslon.judul_paslon,COUNT($this->tableVoting.id_voting) as suara");
//		$//this->db->join($this->tableVoting, "$this->tableVoting.id_paslon=$this->tablePaslon.id_paslon", "full");
		$this->db->where("$this->tablePaslon.id_kategori", $idKategori);
		//$this->db->group_by("$this->tablePaslon.id_voting");
		return $this->db->get($this->tablePaslon);
//		return $this->db->where("$this->tablePaslon.id_kategori", $idKategori)
//			//->order_by("user_listings.list_created", "desc")
////			->limit(18, null)
//			->select("$this->tablePaslon.judul_paslon,COUNT($this->tableVoting.id_voting) as suara")
////			->select('COUNT(user_bookings.book_listing) as totalBooked')
//			->from($this->tablePaslon)
//			->join('voting', 'voting.id_kategori = paslon.id_kategori', 'inner')
//			//->join("(SELECT COUNT(voting.id_voting) FROM voting)) AS suara" ,"voting.id_paslon = paslon.id_paslon")
//
//			->get();
	}



	/** *******************************************************************************
	 * Tabel Otorisasi
	 *  *******************************************************************************
	 */
	public function get_data_otorisasi($id)
	{
		$this->db->join($this->tableKategori, "$this->tableKategori.id_kategori=$this->tableOtorisasi.id_kategori");
		$this->db->where("$this->tableOtorisasi.id_user",$id);
		$this->db->where("$this->tableOtorisasi.status", 1);
		return $this->db->get($this->tableOtorisasi);
	}

	public function perbaharui_data_otorisasi($data,$id)
	{
		$this->db->where('id_user',$id);
		$this->db->update($this->tableOtorisasi,$data);
	}

	/** *******************************************************************************
	 * Tabel Pencocokan
	 *  *******************************************************************************
	 */
	public function simpan_pencocokan($data)
	{
		$this->db->insert($this->tablePencocokan,$data);
	}

	/** *******************************************************************************
	 * Tabel Photo
	 *  *******************************************************************************
	 */
	public function simpan_photo($data)
	{
		$this->db->insert($this->tablePhoto,$data);
	}

	public function get_data_photo($data)
	{
		$this->db->where('id_user', $data);
		return $this->db->get($this->tablePhoto);
	}


	/** *******************************************************************************
	 * Tabel Album
	 *  *******************************************************************************
	 */

	public function get_data_album($id){
		$this->db->where('id_album', $id);
		return $this->db->get($this->tableAlbum);
	}

	/** *******************************************************************************
	 * Tabel User
	 *  *******************************************************************************
	 */

	public function update_password($dataPassword,$idUser)
	{
		$this->db->where("id_user",$idUser);
		$this->db->update($this->tableUser,$dataPassword);
	}
	public function update_detail_user($dataDetail,$id)
	{
		$this->db->where("id_user",$id);
		return $this->db->update($this->tableDetailUser,$dataDetail);
	}

	public function cekEmail($data)
	{
		$this->db->join($this->tableDetailUser, "$this->tableDetailUser.id_user = $this->tableUser.id_user");
		$this->db->where('email', $data);
		return $this->db->get($this->tableUser);
	}

	public function simpan_data_user($data)
	{
		$this->db->insert($this->tableUser,$data);
		return $this->db->insert_id();
	}

	public function simpan_data_detail($data)
	{
		$this->db->insert($this->tableDetailUser,$data);
	}

	public function validasi_login($email, $password)
	{
		$this->db->where('email', $email);
		$this->db->where('password', $password);
		return $this->db->get($this->tableUser)->num_rows();
	}

	public function simpan_token_login($data,$id)
	{
		$this->db->where('id_user',$id);
		$this->db->update($this->tableUser,$data);
	}

	public function get_data_user($id){
		$this->db->where('id_user',$id);
		return $this->db->get($this->tableUser);
	}
	public function get_data_akun($id){
		$this->db->join($this->tableDetailUser, "$this->tableDetailUser.id_user = $this->tableUser.id_user");
		$this->db->where("$this->tableUser.id_user",$id);
		return $this->db->get($this->tableUser);
	}


	/** *******************************************************************************
	 * Tabel Kategori
	 *  *******************************************************************************
	 */


	public function get_data_kategori_hasil()
	{
		$this->db->where('status_kategori', 2);
		return $this->db->get($this->tableKategori);
	}

	/** *******************************************************************************
	 * Tabel Paslon
	 *  *******************************************************************************
	 */


	public function get_data_paslon($id)
	{
		$this->db->join($this->tableDetailPaslon, 'detail_paslon.id_paslon=paslon.id_paslon');
		$this->db->where('paslon.id_kategori', $id);
		return $this->db->get($this->tablePaslon);
	}

	public function get_data_paslon2($id)
	{
		$this->db->where('paslon.id_paslon', $id);
		return $this->db->get($this->tablePaslon);
	}

	public function perbaharui_paslon($dataPaslon,$idPaslon)
	{
		$this->db->where('id_paslon',$idPaslon);
		$this->db->update($this->tablePaslon,$dataPaslon);
	}


	/** *******************************************************************************
	 * Tabel detailpaslon
	 *  *******************************************************************************
	 */


	public function get_detail_paslon($idPaslon){
		$this->db->join($this->tablePaslon, 'paslon.id_paslon=detail_paslon.id_paslon');
		$this->db->where('detail_paslon.id_paslon',$idPaslon);
		return $this->db->get($this->tableDetailPaslon);
	}


}
