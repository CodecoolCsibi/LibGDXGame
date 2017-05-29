
class Team {
    constructor(name) {
        this.name = name;
    }
    toString() {
        return `Team.${this.name}`;
    }
}
Team.RED = new Team('RED');
Team.BLUE = new Team('BLUE');


