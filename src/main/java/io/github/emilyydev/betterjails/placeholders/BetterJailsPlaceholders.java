//
// This file is part of BetterJails-Placeholders, licensed under the MIT License.
//
// Copyright (c) emilyy-dev
// Copyright (c) contributors
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package io.github.emilyydev.betterjails.placeholders;

import com.github.fefo.betterjails.api.BetterJails;
import com.github.fefo.betterjails.api.model.jail.Jail;
import com.github.fefo.betterjails.api.model.prisoner.Prisoner;
import com.github.fefo.betterjails.api.model.prisoner.PrisonerManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;

import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.stream.Collectors.joining;
import static me.clip.placeholderapi.util.TimeUtil.getTime;

public final class BetterJailsPlaceholders extends PlaceholderExpansion {

  private static final String BETTER_JAILS = "BetterJails";
  private static final String IDENTIFIER = BETTER_JAILS.toLowerCase(Locale.ROOT);
  private static final Map<String, Function<OfflinePlayer, String>> PLACEHOLDER_FUNCTIONS;
  private static final List<String> PLACEHOLDERS;
  private static final Function<OfflinePlayer, String> NIL_FUNCTION = $ -> null;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US);

  static {
    PLACEHOLDER_FUNCTIONS =
        ImmutableMap.<String, Function<OfflinePlayer, String>>builder()
            .put("prisoner_time_remaining", prisoner(prisoner -> getTime(between(now(), prisoner.jailedUntil()))))
            .put("prisoner_release_date", prisoner(prisoner -> formatWith(prisoner.jailedUntil(), DATE_FORMATTER)))
            .put("prisoner_release_time", prisoner(prisoner -> formatWith(prisoner.jailedUntil(), TIME_FORMATTER)))
            .put("prisoner_jail_name", prisoner(prisoner -> prisoner.jail().name()))
            .put("prisoner_jailed_by", prisoner(Prisoner::jailedBy))
            .put("prisoner_primary_group", prisoner(Prisoner::primaryGroup))
            .put("prisoner_imprisonment_reason", prisoner(Prisoner::imprisonmentReason))
            .put("prisoner_total_sentence_time", prisoner(prisoner -> getTime(prisoner.totalSentenceTime())))
            .put("list_jails", $ -> betterJails().getJailManager().getAllJails().stream().map(Jail::name).collect(joining(", ")))
            .put("jail_count", $ -> String.valueOf(betterJails().getJailManager().getAllJails().size()))
            .build();

    PLACEHOLDERS =
        PLACEHOLDER_FUNCTIONS.keySet().stream()
            .map(placeholder -> '%' + IDENTIFIER + '_' + placeholder + '%')
            .collect(
                Collector.of(
                    ImmutableList::<String>builder,
                    ImmutableList.Builder::add,
                    (first, second) -> first.addAll(second.build()),
                    ImmutableList.Builder::build
                )
            );
  }

  private static Function<OfflinePlayer, String> prisoner(final Function<Prisoner, String> function) {
    final PrisonerManager prisonerManager = betterJails().getPrisonerManager();
    return player -> Optional.ofNullable(player)
        .map(OfflinePlayer::getUniqueId)
        .map(prisonerManager::getPrisoner)
        .map(function)
        .orElse(null);
  }

  private static BetterJails betterJails() {
    return Bukkit.getServicesManager().load(BetterJails.class);
  }

  private static String formatWith(final Instant instant, final DateTimeFormatter formatter) {
    return formatter.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
  }

  @Override
  public String onRequest(final OfflinePlayer player, final @NotNull String params) {
    return PLACEHOLDER_FUNCTIONS.getOrDefault(params, NIL_FUNCTION).apply(player);
  }

  @Override
  public @NotNull List<String> getPlaceholders() {
    return PLACEHOLDERS;
  }

  @Override
  public @NotNull String getRequiredPlugin() {
    return BETTER_JAILS;
  }

  @Override
  public @NotNull String getIdentifier() {
    return IDENTIFIER;
  }

  @Override
  public @NotNull String getAuthor() {
    return "emilyy-dev";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.1.0";
  }
}
