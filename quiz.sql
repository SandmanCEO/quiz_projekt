-- phpMyAdmin SQL Dump
-- version 4.8.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Czas generowania: 11 Sty 2019, 19:15
-- Wersja serwera: 10.1.37-MariaDB
-- Wersja PHP: 7.3.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `quiz`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `answers`
--

CREATE TABLE `answers` (
  `query_id` int(11) NOT NULL,
  `question_no` int(11) NOT NULL,
  `answer_text` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `answer_no` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Zrzut danych tabeli `answers`
--

INSERT INTO `answers` (`query_id`, `question_no`, `answer_text`, `answer_no`) VALUES
(1, 1, 'odpowiedz 1', 1),
(1, 1, 'odpowiedz 2', 2),
(1, 1, 'odpowiedz 3', 3),
(1, 1, 'odpowiedz 4', 4),
(1, 2, 'drugie pytanie 1', 1),
(1, 2, 'drugie pytanie 2', 2),
(1, 2, 'drugie pytanie 3', 3);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `query`
--

CREATE TABLE `query` (
  `id` int(11) NOT NULL,
  `tittle` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `no_questions` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Zrzut danych tabeli `query`
--

INSERT INTO `query` (`id`, `tittle`, `no_questions`) VALUES
(1, 'Siemasiema', 12),
(2, 'OKdkosan', 4),
(3, 'Okoń łóżko ęśko', 4),
(4, 'Siemasiemasiema siemasiema', 10);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `question`
--

CREATE TABLE `question` (
  `query_id` int(11) NOT NULL,
  `question_text` varchar(100) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `question_no` int(11) NOT NULL,
  `answers_no` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Zrzut danych tabeli `question`
--

INSERT INTO `question` (`query_id`, `question_text`, `question_no`, `answers_no`) VALUES
(1, 'Jakie jest pytanie?', 1, 4),
(1, 'Jakie jest drugie pytanie?', 2, 3);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user`
--

CREATE TABLE `user` (
  `login` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `password` varchar(30) COLLATE utf16_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;

--
-- Zrzut danych tabeli `user`
--

INSERT INTO `user` (`login`, `password`) VALUES
('ankieter', 'pass'),
('siema', 'eniu');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user_answer`
--

CREATE TABLE `user_answer` (
  `user_login` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `query_id` int(11) NOT NULL,
  `question_no` int(11) NOT NULL,
  `answer_text` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Zrzut danych tabeli `user_answer`
--

INSERT INTO `user_answer` (`user_login`, `query_id`, `question_no`, `answer_text`) VALUES
('siema', 1, 1, 'odpowiedz 2'),
('siema', 1, 1, 'odpowiedz 4'),
('siema', 1, 1, 'odpowiedz 4'),
('siema', 1, 1, 'odpowiedz 1'),
('siema', 1, 2, 'drugie pytanie 2'),
('siema', 1, 1, 'odpowiedz 2'),
('siema', 1, 1, 'odpowiedz 2'),
('siema', 1, 2, 'drugie pytanie 3'),
('siema', 1, 1, 'odpowiedz 4'),
('siema', 1, 2, 'drugie pytanie 1');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user_query`
--

CREATE TABLE `user_query` (
  `user_login` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `query_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `answers`
--
ALTER TABLE `answers`
  ADD KEY `query_id` (`query_id`);

--
-- Indeksy dla tabeli `query`
--
ALTER TABLE `query`
  ADD PRIMARY KEY (`id`);

--
-- Indeksy dla tabeli `question`
--
ALTER TABLE `question`
  ADD KEY `query_id` (`query_id`);

--
-- Indeksy dla tabeli `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`login`);

--
-- Indeksy dla tabeli `user_query`
--
ALTER TABLE `user_query`
  ADD KEY `user_login` (`user_login`,`query_id`),
  ADD KEY `query_id` (`query_id`);

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `answers`
--
ALTER TABLE `answers`
  ADD CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`query_id`) REFERENCES `query` (`id`);

--
-- Ograniczenia dla tabeli `question`
--
ALTER TABLE `question`
  ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`query_id`) REFERENCES `query` (`id`);

--
-- Ograniczenia dla tabeli `user_query`
--
ALTER TABLE `user_query`
  ADD CONSTRAINT `user_query_ibfk_1` FOREIGN KEY (`user_login`) REFERENCES `user` (`login`),
  ADD CONSTRAINT `user_query_ibfk_2` FOREIGN KEY (`query_id`) REFERENCES `query` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
