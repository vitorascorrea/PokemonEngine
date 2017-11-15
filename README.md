# PokemonEngine

Engine de batalha Pokémon utilizando regras da Gen7/Smogon com o objetivo de criar uma Inteligência Artificial que consiga ganhar em alto nível competitivo na plataforma [Pokémon Showdown!](http://play.pokemonshowdown.com/).

##Instruções de Instalação (Ubuntu)

1. Tenha Java 8 instalado.

2. Ter o PostgreSQL instalado.

3. Clone o projeto.

3. `cd` na pasta do projeto, crie um banco de dados chamado `pokemon` no seu PostgreSQL e rode `psql pokemon < database.sql`

4. Para compilar: `javac -classpath .:lib/json-simple-master.jar:lib/postgresql-42.1.4.jar:lib/spark-core-2.7.0.jar engine/**/*.java engine/*.java -d bin`

5. Para executar: `java -classpath .:lib/json-simple-master.jar:lib/postgresql-42.1.4.jar:lib/spark-core-2.7.0.jar:bin engine/Main`

##Como funciona

Temos duas partes nesse projeto: a **engine**, que simula as mecânicas de batalhas do Pokémon, e o **decision maker**, que escolhe quais ações executar na batalha (simulando um jogador real).

Um conceito importante da **engine** é o conceito de **State**, que é um objeto que guarda o estado atual do jogo: quais Pokémons estão vivos, qual é o Pokémon ativo de cada time, etc.

Existem implementados alguns **decision makers**, mas os mais relevantes são:

 - Random - escolhe um movimento aleatório dentre os movimentos possíveis
 - Simple Greedy - escolhe o movimento que vai gerar a maior diferença total entre HP dos times

Para implementar um novo **decision maker**, é preciso apenas implementar a interface **DecisionMaker** (`engine/decision/`) que recebe dois parâmetros: um objeto do tipo **State** e um inteiro *1* ou *2* que representa o time do usuário daquele decision maker (importante pois a engine trabalha com esse conceito de ordem entre os times). Exemplos em `engine/Main.java`

##TODO

- [ ] Documentar o código!
- [ ] Implementar um decision maker humano (com input do usuário)
- [ ] Implementar um **switch** decision maker (atualmente ele escolhe um switch aleatório quando o Pokémon ativo morre)
- [ ] Resolver o bug de geração aleatória de times: às vezes o formato de um Pokémon no arquivo factory-sets.json está fora do padrão, o que gera um erro na execução. Basta executar novamente até que ele consiga gerar um time. O ideal seria refazê-lo com os sets atuais do Smogon.
- [ ] Itens e seus efeitos (Choice Band, Leftovers, etc.)
- [ ] Traits e seus efeitos (Levitate, Flash Fire, etc.)
- [ ] Field Hazards (Stealth Rock, Spikes, etc.)
- [ ] Field Conditions (Hail, Sandstorm, Psychic Terrain, etc.)
- [ ] Efeitos especiais de movimentos (U-turn, Volt Switch, Pursuit, etc.)
- [ ] Z-moves
- [ ] Mega Evolution

##API para jogar no Pokémon Showdown!

Em uma versão antiga dessa Engine (que era em Ruby), foi feito uma API que se comunicava com o bot [https://github.com/Ecuacion/Pokemon-Showdown-Node-Bot](https://github.com/Ecuacion/Pokemon-Showdown-Node-Bot).

Esse bot tem alguns arquivos de configuração de AI que permitiam que o bot batalhasse contra outros jogadores seguindo regras especificadas pelo desenvolvedor, só que elas precisavam ser síncronas e programadas em Node. Como eu queria fazer em uma linguagem diferente, precisei modificar um pouco o código fonte para que o bot aceitasse comunicação assíncrona. Essa modificação ficou um pouco... grosseira e não merecedora da qualidade que é o bot.

Nessa nova versão em Java, pretendo utilizar o microframework [Spark](http://sparkjava.com/) (que já está na lib) para criar essa API, mas após conseguir simular perfeitamente a batalha Pokémon.
