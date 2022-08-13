package com.tbnt.ruby.repo

import com.tbnt.ruby.entity.*

class AudioDataRepo {
    fun fetchData(): List<AudioBook> = listOf(
        AudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Garni temple",
            "$3.50",
            0.3f
        ),
        AudioBook(
            "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9c/Haghartsin_monastery_2015.jpg/640px-Haghartsin_monastery_2015.jpg",
            "Haghartsin",
            "$4.50",
            0.7f
        ),
        AudioBook(
            "https://silkroadarmenia.am/wp-content/uploads/2020/07/GEGHARD-6.jpg",
            "Geghard",
            "5.50",
            0.3f
        ),
        AudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Tatev monastery",
            "7.50",
            0.4f
        ),
        AudioBook(
            "https://silkroadarmenia.am/wp-content/uploads/2020/07/GEGHARD-6.jpg",
            "Garni temple 2",
            "$3.50",
            0.3f
        ),
        AudioBook(
            "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9c/Haghartsin_monastery_2015.jpg/640px-Haghartsin_monastery_2015.jpg",
            "Haghartsin 2",
            "$4.50",
            0.7f
        ),
        AudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524282680672330.jpg",
            "Geghard 2",
            "5.50",
            0.3f
        ),
        AudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524282680672330.jpg",
            "Tatev monastery 3",
            "7.50",
            0.4f
        )
    )

    fun fetchTipsData(): List<Tips> = listOf(
        Tips.Title("Yerevan"),
        Tips.Content("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr."),
        Tips.Content("Nonumy eirmod tempor invidunt sadipscing elitr."),
        Tips.Content("Sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr."),
        Tips.Title("Tatev Monastery"),
        Tips.Content("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr."),
        Tips.Content("Nonumy eirmod tempor invidunt sadipscing elitr."),
    )

    fun fetchMyBooksData(): List<MyAudioBook> = listOf(
        MyAudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Geghard temple",
            "12:35 min",
            0.8f
        ), MyAudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Ani temple",
            "12:35 min",
            0.8f
        ), MyAudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Garni temple",
            "12:35 min",
            0.8f
        ), MyAudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Garni temple",
            "12:35 min",
            0.8f
        ),
        MyAudioBook(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Garni temple",
            "12:35 min",
            0.8f
        )
    )

    fun fetchPreviewData() = AudioPreviewEntity(
        "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
        "Garni temple",
        "8 audiofiles",
        2f,
        "15:30 min",
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr.Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr.",
        "        \"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr.Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr.,         \"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr.Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr.,         \"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr.Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt sadipscing elitr."
    )

    fun fetchSubPackagesById(): List<SubAudioEntity> = listOf(
        SubAudioEntity(
            1,
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Garni Temple Inside",
            "12 min",
        ),
        SubAudioEntity(
            2,
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524282680672330.jpg",
            "Garni temple",
            "2 min",
        ),
        SubAudioEntity(
            3,
            "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9c/Haghartsin_monastery_2015.jpg/640px-Haghartsin_monastery_2015.jpg",
            "Garni temple",
            "15 min",
        ),
        SubAudioEntity(
            4,
            "https://silkroadarmenia.am/wp-content/uploads/2020/07/GEGHARD-6.jpg",
            "Garni temple",
            "5 min",
        ),
    )

    fun getAudioFilesData() = listOf(
        AudioPlayerEntity(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524295874407964.jpg",
            "Garni Temple Inside"
        ),
        AudioPlayerEntity(
            "https://d31qtdfy11mjj9.cloudfront.net/places/1511524282680672330.jpg",
            "Garni Temple Inside"
        ),
    )
}