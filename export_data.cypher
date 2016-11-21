begin
create index on :`AgeGroup`(`id`);
create index on :`Child`(`ageGroupId`);
create index on :`Child`(`id`);
create index on :`Child`(`userId`);
create index on :`Genre`(`id`);
create index on :`User`(`id`);
create index on :`Video`(`userId`);
create index on :`Video`(`videoId`);
create constraint on (n:`AgeGroup`) assert n.`label` is unique;
create constraint on (n:`Child`) assert n.`name` is unique;
create constraint on (n:`Genre`) assert n.`label` is unique;
create constraint on (n:`User`) assert n.`email` is unique;
create constraint on (n:`Video`) assert n.`title` is unique;
create constraint on (n:`Video`) assert n.`video_id` is unique;
commit
begin
create (_0:`Genre` {`created`:"2016-09-14 03:19:12", `id`:1, `name`:"Uncategorized"})
create (_1:`Genre` {`created`:"2016-09-14 03:19:11", `id`:2, `name`:"Fantasy"})
create (_2:`Genre` {`created`:"2016-09-14 03:19:11", `id`:3, `name`:"Sing-along"})
create (_3:`Genre` {`created`:"2016-09-14 03:19:11", `id`:4, `name`:"Animation"})
create (_4:`Genre` {`created`:"2016-09-14 03:19:11", `id`:5, `name`:"3D"})
create (_5:`Genre` {`created`:"2016-09-14 03:19:12", `id`:6, `name`:"4D"})
create (_6:`Genre` {`created`:"2016-09-14 03:19:11", `id`:7, `name`:"Action"})
create (_9:`AgeGroup` {`end`:"5", `id`:1, `label`:"3 to 5 years old", `start`:"3"})
create (_10:`AgeGroup` {`end`:"6", `id`:2, `label`:"4 to 6 years old", `start`:"4"})
create (_11:`AgeGroup` {`end`:"7", `id`:3, `label`:"5 to 7 years old", `start`:"5"})
create (_12:`AgeGroup` {`end`:"8", `id`:4, `label`:"6 to 8 years old", `start`:"6"})
create (_13:`AgeGroup` {`end`:"9", `id`:5, `label`:"7 to 9 years old", `start`:"7"})
create (_14:`AgeGroup` {`end`:"10", `id`:6, `label`:"8 to 10 years old", `start`:"8"})
create (_15:`AgeGroup` {`end`:"11", `id`:7, `label`:"9 to 11 years old", `start`:"9"})
;
commit