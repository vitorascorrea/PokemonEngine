# PokemonEngine

Engine de batalha Pokémon utilizando regras da Gen7/Smogon.

**Instruções de Instalação (Ubuntu)**

1. Tenha Java 8 instalado.

2. Ter o PostgreSQL instalado.

3. Clone o projeto.

3. `cd` na pasta do projeto, crie um banco de dados chamado `pokemon` no seu PostgreSQL e rode `psql pokemon < database.sql`

4. Para compilar: `javac -classpath .:lib/json-simple-master.jar:lib/postgresql-42.1.4.jar:lib/spark-core-2.7.0.jar engine/**/*.java engine/*.java -d bin`

5. Para executar: `java -classpath .:lib/json-simple-master.jar:lib/postgresql-42.1.4.jar:lib/spark-core-2.7.0.jar:bin engine/Main`

**TODO**

- [ ] Itens e seus efeitos (Choice Band, Leftovers, etc.)
- [ ] Traits e seus efeitos (Levitate, Flash Fire, etc.)
- [ ] Field Hazards (Stealth Rock, Spikes, etc.)
- [ ] Field Conditions (Hail, Sandstorm, Psychic Terrain, etc.)
- [ ] Efeitos especiais de movimentos (U-turn, Volt Switch, Pursuit, etc.)
- [ ] Z-moves
- [ ] Mega Evolution

**API para jogar no Pokémon Showdown!**

Em uma versão antiga dessa Engine (que era em Ruby), foi feito uma API que se comunicava com o bot [https://github.com/Ecuacion/Pokemon-Showdown-Node-Bot](https://github.com/Ecuacion/Pokemon-Showdown-Node-Bot). 

Esse bot tem alguns arquivos de configuração de AI que permitiam que o bot batalhasse contra outros jogadores seguindo regras especificadas pelo desenvolvedor, só que elas precisavam ser síncronas e programadas em Node. Como eu queria fazer em uma linguagem diferente, precisei modificar um pouco o código fonte para que o bot aceitasse comunicação assíncrona. Essa modificação ficou um pouco... grosseira e não merecedora da qualidade que é o bot.

Nessa nova versão em Java, pretendo utilizar o microframework [Spark](http://sparkjava.com/) (que já está na lib) para criar essa API, mas após conseguir simular perfeitamente a batalha Pokémon.
