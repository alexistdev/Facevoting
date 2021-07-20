-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 20, 2021 at 02:58 AM
-- Server version: 10.4.14-MariaDB
-- PHP Version: 7.4.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `evoting`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id_admin` int(11) NOT NULL,
  `username` varchar(30) NOT NULL,
  `password` varchar(255) NOT NULL,
  `status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id_admin`, `username`, `password`, `status`) VALUES
(1, 'admin', '$2y$10$lqkCunzVQwEvp7WPZWuQlOLHTDiq1JQ9GpyTNfMaW3bFwaAerLEAW', 1);

-- --------------------------------------------------------

--
-- Table structure for table `album`
--

CREATE TABLE `album` (
  `id_album` int(11) NOT NULL,
  `nama_album` varchar(255) NOT NULL,
  `kode_album` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `album`
--

INSERT INTO `album` (`id_album`, `nama_album`, `kode_album`) VALUES
(3, 'Facevoting2021', '859d15e7156ef8128f921671b6a3d941a4a7f686b4e762bbc679c4809bdebb19');

-- --------------------------------------------------------

--
-- Table structure for table `detail_paslon`
--

CREATE TABLE `detail_paslon` (
  `id_detailpaslon` int(11) NOT NULL,
  `id_paslon` int(11) NOT NULL,
  `visi_misi` text NOT NULL,
  `profil_catum` text NOT NULL,
  `profil_cawatum` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `detail_paslon`
--

INSERT INTO `detail_paslon` (`id_detailpaslon`, `id_paslon`, `visi_misi`, `profil_catum`, `profil_cawatum`) VALUES
(2, 1, 'Mencerdaskan kehidupan Bangsa', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s'),
(3, 2, 'Mencerdaskan kehidupan Bangsa', 'Rasmus Lerdorf adalah bapak PHP kita.', 'Brendan Eich adalah bapak Javascript kita.'),
(4, 3, 'Mencerdaskan kehidupan Bangsa', 'Rasmus Lerdorf adalah bapak PHP kita.', 'Brendan Eich adalah bapak Javascript kita.');

-- --------------------------------------------------------

--
-- Table structure for table `detail_user`
--

CREATE TABLE `detail_user` (
  `id_detail` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `identitas` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `detail_user`
--

INSERT INTO `detail_user` (`id_detail`, `id_user`, `nama`, `identitas`) VALUES
(10, 10, 'Alexsander Hendra Wijaya', '123456');

-- --------------------------------------------------------

--
-- Table structure for table `kategori`
--

CREATE TABLE `kategori` (
  `id_kategori` int(11) NOT NULL,
  `nama_kategori` varchar(80) NOT NULL,
  `logo_kategori` varchar(100) NOT NULL,
  `status_kategori` int(11) NOT NULL COMMENT '1 = open\r\n2 = ditutup\r\n3 = tidak tersedia'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `kategori`
--

INSERT INTO `kategori` (`id_kategori`, `nama_kategori`, `logo_kategori`, `status_kategori`) VALUES
(8, 'BEM', 'bem_logo.png', 2),
(9, 'HIMKRIS', 'himkris.png', 2),
(10, 'UKM MUSIC', '9b2a96dee3.png', 1);

-- --------------------------------------------------------

--
-- Table structure for table `otorisasi_pemilih`
--

CREATE TABLE `otorisasi_pemilih` (
  `id_otorisasi` int(11) NOT NULL,
  `id_user` int(11) DEFAULT NULL,
  `id_kategori` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `otorisasi_pemilih`
--

INSERT INTO `otorisasi_pemilih` (`id_otorisasi`, `id_user`, `id_kategori`, `status`) VALUES
(8, 10, 8, 1),
(9, 10, 9, 1);

-- --------------------------------------------------------

--
-- Table structure for table `paslon`
--

CREATE TABLE `paslon` (
  `id_paslon` int(11) NOT NULL,
  `id_kategori` int(11) NOT NULL,
  `judul_paslon` varchar(20) NOT NULL,
  `ketua_paslon` varchar(50) NOT NULL,
  `wakil_paslon` varchar(50) NOT NULL,
  `photo1_paslon` varchar(100) DEFAULT NULL,
  `photo2_paslon` varchar(100) DEFAULT NULL,
  `perolehan` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `paslon`
--

INSERT INTO `paslon` (`id_paslon`, `id_kategori`, `judul_paslon`, `ketua_paslon`, `wakil_paslon`, `photo1_paslon`, `photo2_paslon`, `perolehan`) VALUES
(1, 8, 'Paslon 1', 'Steve Job', 'Steve Wozniak', 'stevejob.jpg', 'stevewozniak.jpg', 0),
(2, 9, 'Paslon 1', 'Rasmus Lerdorf', 'Brendan Eich', 'rasmus.jpg', 'brendan.jpg', 1),
(3, 9, 'Paslon 2', 'Rasmus Lerdorf', 'Brendan Eich', 'rasmus.jpg', 'brendan.jpg', 0);

-- --------------------------------------------------------

--
-- Table structure for table `pencocokan`
--

CREATE TABLE `pencocokan` (
  `id_pencocokan` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `nama_photo` varchar(100) NOT NULL,
  `gambar` varchar(128) NOT NULL,
  `score` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `pencocokan`
--

INSERT INTO `pencocokan` (`id_pencocokan`, `id_user`, `nama_photo`, `gambar`, `score`) VALUES
(1, 4, 'b6a0595f28', 'b6a0595f28.jpg', ''),
(2, 4, '9930a54453', '9930a54453.jpg', ''),
(3, 4, 'fe82185fc4', 'fe82185fc4.jpg', '');

-- --------------------------------------------------------

--
-- Table structure for table `photo`
--

CREATE TABLE `photo` (
  `id_photo` int(11) NOT NULL,
  `id_album` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `entry_id` varchar(100) NOT NULL,
  `nama_photo` varchar(100) NOT NULL,
  `gambar` varchar(128) NOT NULL,
  `status_train` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `tbjurusan`
--

CREATE TABLE `tbjurusan` (
  `id_jurusan` int(11) NOT NULL,
  `kode_jurusan` varchar(50) NOT NULL,
  `nama_jurusan` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tbjurusan`
--

INSERT INTO `tbjurusan` (`id_jurusan`, `kode_jurusan`, `nama_jurusan`) VALUES
(1, 'SKO123456', 'sistem informasi'),
(2, 'SKO12456', 'teknik informatika');

-- --------------------------------------------------------

--
-- Table structure for table `token`
--

CREATE TABLE `token` (
  `id_token` int(11) NOT NULL,
  `id_admin` int(11) NOT NULL,
  `token` varchar(100) NOT NULL,
  `time` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `token`
--

INSERT INTO `token` (`id_token`, `id_admin`, `token`, `time`) VALUES
(25, 1, 'e4cc5f92bbabc88c53c530bda8e56ad83383a164', 1616326513);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `token_firebase` varchar(255) DEFAULT NULL,
  `token_login` varchar(100) DEFAULT NULL,
  `entry_id` varchar(255) NOT NULL,
  `status` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `email`, `password`, `token_firebase`, `token_login`, `entry_id`, `status`) VALUES
(10, 'alexistdev@gmail.com', '325339', 'a1231231', 'ada1231', '1231231', 1);

-- --------------------------------------------------------

--
-- Table structure for table `voting`
--

CREATE TABLE `voting` (
  `id_voting` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `id_kategori` int(11) NOT NULL,
  `id_paslon` int(11) NOT NULL,
  `tanggal_voting` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_admin`);

--
-- Indexes for table `album`
--
ALTER TABLE `album`
  ADD PRIMARY KEY (`id_album`);

--
-- Indexes for table `detail_paslon`
--
ALTER TABLE `detail_paslon`
  ADD PRIMARY KEY (`id_detailpaslon`),
  ADD KEY `id_paslon` (`id_paslon`);

--
-- Indexes for table `detail_user`
--
ALTER TABLE `detail_user`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `kategori`
--
ALTER TABLE `kategori`
  ADD PRIMARY KEY (`id_kategori`);

--
-- Indexes for table `otorisasi_pemilih`
--
ALTER TABLE `otorisasi_pemilih`
  ADD PRIMARY KEY (`id_otorisasi`),
  ADD KEY `id_user` (`id_user`),
  ADD KEY `id_kategori` (`id_kategori`);

--
-- Indexes for table `paslon`
--
ALTER TABLE `paslon`
  ADD PRIMARY KEY (`id_paslon`),
  ADD KEY `id_kategori` (`id_kategori`);

--
-- Indexes for table `pencocokan`
--
ALTER TABLE `pencocokan`
  ADD PRIMARY KEY (`id_pencocokan`);

--
-- Indexes for table `photo`
--
ALTER TABLE `photo`
  ADD PRIMARY KEY (`id_photo`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `tbjurusan`
--
ALTER TABLE `tbjurusan`
  ADD PRIMARY KEY (`id_jurusan`);

--
-- Indexes for table `token`
--
ALTER TABLE `token`
  ADD PRIMARY KEY (`id_token`),
  ADD KEY `token_ibfk_1` (`id_admin`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`);

--
-- Indexes for table `voting`
--
ALTER TABLE `voting`
  ADD PRIMARY KEY (`id_voting`),
  ADD KEY `id_user` (`id_user`),
  ADD KEY `id_kategori` (`id_kategori`),
  ADD KEY `id_paslon` (`id_paslon`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `id_admin` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `album`
--
ALTER TABLE `album`
  MODIFY `id_album` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `detail_paslon`
--
ALTER TABLE `detail_paslon`
  MODIFY `id_detailpaslon` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `detail_user`
--
ALTER TABLE `detail_user`
  MODIFY `id_detail` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `kategori`
--
ALTER TABLE `kategori`
  MODIFY `id_kategori` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `otorisasi_pemilih`
--
ALTER TABLE `otorisasi_pemilih`
  MODIFY `id_otorisasi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `paslon`
--
ALTER TABLE `paslon`
  MODIFY `id_paslon` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `pencocokan`
--
ALTER TABLE `pencocokan`
  MODIFY `id_pencocokan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `photo`
--
ALTER TABLE `photo`
  MODIFY `id_photo` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `tbjurusan`
--
ALTER TABLE `tbjurusan`
  MODIFY `id_jurusan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `token`
--
ALTER TABLE `token`
  MODIFY `id_token` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `voting`
--
ALTER TABLE `voting`
  MODIFY `id_voting` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_paslon`
--
ALTER TABLE `detail_paslon`
  ADD CONSTRAINT `detail_paslon_ibfk_1` FOREIGN KEY (`id_paslon`) REFERENCES `paslon` (`id_paslon`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `detail_user`
--
ALTER TABLE `detail_user`
  ADD CONSTRAINT `detail_user_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `otorisasi_pemilih`
--
ALTER TABLE `otorisasi_pemilih`
  ADD CONSTRAINT `otorisasi_pemilih_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `otorisasi_pemilih_ibfk_2` FOREIGN KEY (`id_kategori`) REFERENCES `kategori` (`id_kategori`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `paslon`
--
ALTER TABLE `paslon`
  ADD CONSTRAINT `paslon_ibfk_1` FOREIGN KEY (`id_kategori`) REFERENCES `kategori` (`id_kategori`);

--
-- Constraints for table `photo`
--
ALTER TABLE `photo`
  ADD CONSTRAINT `photo_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `token`
--
ALTER TABLE `token`
  ADD CONSTRAINT `token_ibfk_1` FOREIGN KEY (`id_admin`) REFERENCES `admin` (`id_admin`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Constraints for table `voting`
--
ALTER TABLE `voting`
  ADD CONSTRAINT `voting_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `voting_ibfk_2` FOREIGN KEY (`id_kategori`) REFERENCES `kategori` (`id_kategori`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `voting_ibfk_3` FOREIGN KEY (`id_paslon`) REFERENCES `paslon` (`id_paslon`) ON DELETE CASCADE ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
