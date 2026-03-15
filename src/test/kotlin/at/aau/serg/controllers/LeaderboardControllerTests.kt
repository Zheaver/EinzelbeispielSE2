package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertFailsWith

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_correctTimeSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    @Test
    fun test_getLeaderboard_invalidRankTooSmall() {
        whenever(mockedService.getGameResults()).thenReturn(
            listOf(GameResult(1, "a", 10, 10.0))
        )

        val ex = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(0)
        }

        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
    }

    @Test
    fun test_getLeaderboard_invalidRankTooLarge() {
        whenever(mockedService.getGameResults()).thenReturn(
            listOf(GameResult(1, "a", 10, 10.0))
        )

        val ex = assertFailsWith<ResponseStatusException> {
            controller.getLeaderboard(2)
        }

        assertEquals(HttpStatus.BAD_REQUEST, ex.statusCode)
    }

    @Test
    fun test_getLeaderboard_rankInMiddle_returnsSurroundingEntries() {
        val entries = listOf(
            GameResult(1, "p1", 100, 10.0),
            GameResult(2, "p2", 90, 10.0),
            GameResult(3, "p3", 80, 10.0),
            GameResult(4, "p4", 70, 10.0),
            GameResult(5, "p5", 60, 10.0),
            GameResult(6, "p6", 50, 10.0),
            GameResult(7, "p7", 40, 10.0),
            GameResult(8, "p8", 30, 10.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(entries)

        val res = controller.getLeaderboard(5)

        assertEquals(7, res.size)
        assertEquals("p2", res[0].playerName)
        assertEquals("p8", res[6].playerName)
    }

    @Test
    fun test_getLeaderboard_rankAtBeginning_returnsOnlyAvailableEntries() {
        val entries = listOf(
            GameResult(1, "p1", 100, 10.0),
            GameResult(2, "p2", 90, 10.0),
            GameResult(3, "p3", 80, 10.0),
            GameResult(4, "p4", 70, 10.0),
            GameResult(5, "p5", 60, 10.0)
        )

        whenever(mockedService.getGameResults()).thenReturn(entries)

        val res = controller.getLeaderboard(1)

        assertEquals(4, res.size)
        assertEquals("p1", res[0].playerName)
        assertEquals("p4", res[3].playerName)
    }
}