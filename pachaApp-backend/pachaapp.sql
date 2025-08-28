-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 25-08-2025 a las 02:46:43
-- Versión del servidor: 10.4.27-MariaDB
-- Versión de PHP: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `pachaapp`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `actividades`
--

CREATE TABLE `actividades` (
  `idActividad` char(36) NOT NULL,
  `idUsuario` char(36) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `lugar` varchar(150) DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  `fechaActividad` timestamp NULL DEFAULT NULL,
  `fechaRegistro` timestamp NOT NULL DEFAULT current_timestamp(),
  `fechaActualizacion` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `actividades`
--

INSERT INTO `actividades` (`idActividad`, `idUsuario`, `descripcion`, `lugar`, `estado`, `fechaActividad`, `fechaRegistro`, `fechaActualizacion`) VALUES
('18c19a94-7639-4fde-8160-d281d6d5171d', '0ab5d950-5d01-4ade-995f-77f97efb825d', 'ddddd', 'abancay', 'concluido', '2025-08-07 01:21:00', '2025-08-07 00:19:48', '2025-08-14 22:59:53'),
('22bfb4ac-d9e0-4793-be2a-af5c844015b7', '026809a4-fc78-4883-bafc-712bc5523645', 'ASS', 'cusco', 'concluido', '2025-08-16 02:47:15', '2025-08-16 01:47:25', '2025-08-24 01:47:36'),
('2e3ff41a-c7cc-4d50-8b39-bd760bd205ce', '758da8ea-3013-4745-bfb3-6961d72e2ec9', 'sswwww', 'Abancay', 'concluido', '2025-08-16 01:14:00', '2025-08-16 01:13:28', '2025-08-16 01:14:34'),
('573c8bb7-970f-4a1e-aef2-cf25a25b73f4', '758da8ea-3013-4745-bfb3-6961d72e2ec9', 'ddsas', 'Abancay', 'concluido', '2025-08-16 00:59:00', '2025-08-16 00:57:35', '2025-08-16 00:59:57'),
('5dd18f72-b21e-42ad-ae1e-e57e6b2e984c', '026809a4-fc78-4883-bafc-712bc5523645', 'zzzz', 'cusco', 'concluido', '2025-08-16 02:44:47', '2025-08-16 01:45:05', '2025-08-24 01:47:36'),
('6886fa67-916c-44a7-9b12-123652e07983', '0ab5d950-5d01-4ade-995f-77f97efb825d', 'notificación prueba', 'lima', 'concluido', '2025-08-16 01:48:00', '2025-08-16 00:47:09', '2025-08-16 01:48:02'),
('871e0f14-9eb5-49f6-bf43-ce5fa1d0ed51', '0ab5d950-5d01-4ade-995f-77f97efb825d', 'ir de viaje', 'cusco', 'iniciado', '2025-08-25 16:10:00', '2025-08-24 15:43:25', '2025-08-24 15:43:25'),
('94dbfdf2-e5ee-438f-b66e-e0a45b2e67e4', '026809a4-fc78-4883-bafc-712bc5523645', 'sssa', 'Abancay', 'concluido', '2025-08-16 02:57:37', '2025-08-16 01:57:44', '2025-08-24 01:47:36'),
('95986481-e61c-4507-bf64-8e1ae0f87474', '0ab5d950-5d01-4ade-995f-77f97efb825d', 'wwwwww', 'cusco', 'concluido', '2025-08-14 23:54:00', '2025-08-14 22:53:35', '2025-08-16 00:39:58'),
('9bdf7e7c-1ddb-4237-8b84-82c510cfb344', '758da8ea-3013-4745-bfb3-6961d72e2ec9', 'deessnoti', 'cusco', 'concluido', '2025-08-16 00:57:00', '2025-08-16 00:55:27', '2025-08-16 00:57:57'),
('a8515ce6-484f-4057-b47b-d97e6aba3894', '0ab5d950-5d01-4ade-995f-77f97efb825d', 'aaaaaa', 'Abancay', 'concluido', '2025-08-14 23:45:00', '2025-08-14 22:44:39', '2025-08-16 00:39:58'),
('deb7e04d-e762-42fe-9c3b-7e6e0ddd9cec', '026809a4-fc78-4883-bafc-712bc5523645', 'saa', 'cusco', 'concluido', '2025-08-16 02:59:16', '2025-08-16 01:59:23', '2025-08-24 01:47:36'),
('df89e7b3-4d17-4fc7-ba56-1af48e498b9c', '026809a4-fc78-4883-bafc-712bc5523645', 'wwwww', 'Abancay', 'concluido', '2025-08-16 02:30:04', '2025-08-16 01:30:34', '2025-08-24 01:47:36');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `busquedareciente`
--

CREATE TABLE `busquedareciente` (
  `idBusquedaReciente` char(36) NOT NULL,
  `idUsuario` char(36) DEFAULT NULL,
  `ciudad` varchar(150) DEFAULT NULL,
  `fechaRegistro` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `busquedareciente`
--

INSERT INTO `busquedareciente` (`idBusquedaReciente`, `idUsuario`, `ciudad`, `fechaRegistro`) VALUES
('12e2f060-44ea-402f-9fe8-7f443abe0fdc', '0ab5d950-5d01-4ade-995f-77f97efb825d', 'Lima', '2025-08-24 22:31:25'),
('25f9021c-cc89-488a-8385-f59f92c32b1e', '026809a4-fc78-4883-bafc-712bc5523645', 'Lima', '2025-08-24 01:56:45'),
('9486e89f-5ede-4eda-b4a0-6c654065bc2f', '0ab5d950-5d01-4ade-995f-77f97efb825d', 'Cusco', '2025-08-24 22:29:08'),
('a6dcf1d1-1e5b-4034-9a7e-33df3d59e331', '0ab5d950-5d01-4ade-995f-77f97efb825d', 'Abancay', '2025-08-24 22:10:16');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `idUsuario` char(36) NOT NULL,
  `email` varchar(120) DEFAULT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `apellido` varchar(150) DEFAULT NULL,
  `foto` varchar(255) DEFAULT NULL,
  `firebaseUid` varchar(128) NOT NULL,
  `fechaRegistro` datetime DEFAULT NULL,
  `fcmToken` varchar(255) DEFAULT NULL,
  `fechaActualizacionToken` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`idUsuario`, `email`, `nombre`, `apellido`, `foto`, `firebaseUid`, `fechaRegistro`, `fcmToken`, `fechaActualizacionToken`) VALUES
('026809a4-fc78-4883-bafc-712bc5523645', 'rscuak@gmail.com', 'Raul', 'Sanchez', 'https://anvdtwwhaqhhueiqrtjn.supabase.co/storage/v1/object/public/perfilpachaapp/foto/026809a4-fc78-4883-bafc-712bc5523645.png', 'nLXOOwPr8WYNrMZOhBwVwSM9m5I2', '2025-08-15 20:29:42', 'cRmkftbWSEKYlrlfg6Z0Kg:APA91bHLr_BBCTyr8o9kH2MknnjTQRLaLf1mcYAT9GCdsMPGhqWSm_ICrR7Vf3CeArE6sW50hCzJ7nrwUUAuN5urC3nQ90vleHHWAajkvS7vvq6JsIaigbk', '2025-08-24 01:56:13'),
('0ab5d950-5d01-4ade-995f-77f97efb825d', 'oliverachavezcristian@gmail.com', 'Cristian', 'Oner', 'https://anvdtwwhaqhhueiqrtjn.supabase.co/storage/v1/object/public/perfilpachaapp/foto/0ab5d950-5d01-4ade-995f-77f97efb825d.png', 'x5kP3KVcCpedaxwAcGizDjLNGLM2', '2025-08-06 19:18:30', 'fXZN05WBTqSYEGCozWobrE:APA91bECrKM38SzKmrnoKYn0W_2euB1Q0JdM8I6saSGb_-gr-iyvDOffPI9ih_WSLSvbUjsQ0NorGhtDG24n0eNzc-RY5jOkVUiLfbS2ypfAetrsrhd0IPs', '2025-08-24 22:34:59'),
('758da8ea-3013-4745-bfb3-6961d72e2ec9', '63289575@pronabec.edu.pe', 'CRISTIAN', 'OLIVERA CHAVEZ', 'https://anvdtwwhaqhhueiqrtjn.supabase.co/storage/v1/object/public/perfilpachaapp/foto/758da8ea-3013-4745-bfb3-6961d72e2ec9.png', 'cnrnpn8zc7ReQm8AkcwK1R3IoIy1', '2025-08-15 19:53:58', 'cRmkftbWSEKYlrlfg6Z0Kg:APA91bHLr_BBCTyr8o9kH2MknnjTQRLaLf1mcYAT9GCdsMPGhqWSm_ICrR7Vf3CeArE6sW50hCzJ7nrwUUAuN5urC3nQ90vleHHWAajkvS7vvq6JsIaigbk', '2025-08-16 01:22:06');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `actividades`
--
ALTER TABLE `actividades`
  ADD PRIMARY KEY (`idActividad`),
  ADD KEY `actividades_idUsuario_fk` (`idUsuario`);

--
-- Indices de la tabla `busquedareciente`
--
ALTER TABLE `busquedareciente`
  ADD PRIMARY KEY (`idBusquedaReciente`),
  ADD KEY `busquedaReciente_idUsuario_fk` (`idUsuario`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`idUsuario`);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `actividades`
--
ALTER TABLE `actividades`
  ADD CONSTRAINT `actividades_idUsuario_fk` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);

--
-- Filtros para la tabla `busquedareciente`
--
ALTER TABLE `busquedareciente`
  ADD CONSTRAINT `busquedaReciente_idUsuario_fk` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idUsuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
