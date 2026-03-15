package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {

        val leaderboard = gameResultService.getGameResults()
            .sortedWith(
                compareByDescending<GameResult> { it.score }
                    .thenBy { it.timeInSeconds }
            )

        if (rank == null) {
            return leaderboard
        }

        if (rank < 1 || rank > leaderboard.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank")
        }

        val start = maxOf(0, rank - 4)
        val end = minOf(leaderboard.size, rank + 3)

        return leaderboard.subList(start, end)
    }
}