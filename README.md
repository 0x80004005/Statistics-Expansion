# Statistics-Expansion
Adds placeholders for minecraft statistics. Unlike the existing [Statistics-Expansion](https://github.com/PlaceholderAPI/Statistics-Expansion), this forked and modified version returns values for an entire server population instead of per player. 

# Installation

1. Place Expansion-AggStat.jar in the servers /plugins/PlaceholderAPI/expansions folder
2. Run /papi register Expansion-AggStat.jar

#  Usage

In my scenario, I'm using [DecentHolograms](https://www.spigotmc.org/resources/decent-holograms-1-8-1-18-papi-support-no-dependencies.96927/) to display the placeholder data in a spawn base. One of the primary reasons for using DecentHolograms is the ability to control refresh-rate of the placeholder. Due to the nature of the plugin, and the fact that the values are returned for an entire server population, controlloing how ofter the placeholder is updated keeps performance impacts low. Once a Hologram is created modifying the update-interval option is advised. Considering these are server-wide statistics I tend to use high refresh intervals as I don't need to know in real-time when someone jumps, mines a block type, etc. 

Due to being built upon the existing Statistics-Expansion plugin requesting aggregate data requires a modified identifier, but the remaining commands structure is the same as the existing Statistics-Expansion. 

Examples:

1. To know how many creepers the sever has killed: %agstat_kill_entity:creeper%
2. To know how many blocks of dirt the server has mined: %math_0_({agstat_mine_block:dirt} + {agstat_mine_block:grass})%
     - Note this example uses the [math expansion](https://github.com/Andre601/Math-Expansion) because we combined the agstat value of dirt, and the agstat value of grass, thereby considering dirt to be the cumulative of those two block types. This same idea can be used to combine deepslate + regular ore types when returning iron, copper, etc in 1.18+

3. To know how many times players have jumped: %agstat_jump%
4. To know how many times players have died: %agstat_deaths%

For additional examples review the existing documentation for [Statistics-Expansion](https://github.com/PlaceholderAPI/Statistics-Expansion/wiki#default-statistic-placeholders) and replace the identifier of %statistic_ with %aggstat_

# In-Game
If you'd like you can join the server and check it out there, or just peep the screenshots below. Server IP: runcmd.medievalknievel.gg

![2022-02-09_11 49 19](https://user-images.githubusercontent.com/88350295/153251685-8b18bab7-9095-4f54-b7e6-94500359b30d.png)

![2022-02-09_11 49 26](https://user-images.githubusercontent.com/88350295/153251752-1ffffff5-a9f6-4d6a-9c45-d8e70f939d40.png)

![2022-02-09_11 49 35](https://user-images.githubusercontent.com/88350295/153251758-d43efed8-3f88-4106-b31d-d55c22e51184.png)

![2022-02-09_11 49 40](https://user-images.githubusercontent.com/88350295/153251777-72486a26-46b0-42a3-80f0-cfb731aebf36.png)

![2022-02-09_12 04 05](https://user-images.githubusercontent.com/88350295/153251870-6446354d-98d3-44c0-91e6-92dbfdff263a.png)

![2022-02-09_12 04 11](https://user-images.githubusercontent.com/88350295/153251876-c0e711e1-850e-4d0b-b665-2ce0de9a35ba.png)
